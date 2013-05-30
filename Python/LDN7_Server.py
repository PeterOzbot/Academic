#coding:UTF-8
import struct
import socket
import ssl
import xml.dom.minidom
import sys

class Server:
    # maksimalna dolžina XMLja je 16 bit ushort (2 Bytes) = max. 64KB dolgo sporocilo
    HEADER_LENGTH = 2

    # pot do certifikatov ki jim zaupamo
    TRUSTEDCERTPATH=""
    # pot do javnega kljuca serverja
    SERVERPUBLICCERTPATH=""
    # pot do privatnega kljuca serverja
    SERVERPRIVATECERTPATH=""

    # stanje
    STANJA={}

    # ime trenutnega uporabnika
    USER=""

    def __init__(self, server_address):
        self.server_address = server_address

    def receive_fixed_length_msg(self, sock, msglen):
        msg = b''
        while len(msg) < msglen:
            chunk = sock.recv(msglen - len(msg))
            if chunk == b'':
                raise RuntimeError("socket connection broken")
            msg = msg + chunk
        return msg

    def receive_msg(self, sock):
        msg = None
        # preberi glavo sporocila (v prvih 2 bytih je dolzina sporocila)
        header = self.receive_fixed_length_msg(sock, self.HEADER_LENGTH)
        # pretvori v int
        msg_len = struct.unpack("!H", header)[0]
        # preberi XML sporocilo, ce je z glavo vse OK
        if msg_len > 0:
            msg = self.receive_fixed_length_msg(sock, msg_len)
            msg = msg.decode("utf-8")
        return msg

    def send_msg(self, sock, msg):
        #pretvori msg v niz bytov, uporabi UTF-8 kodno tabelo
        encoded_msg = msg.encode("utf-8")
        # ustvari glavo v prvih 2 bytih je dolzina sporocila (HEADER_LENGTH)
        #  metoda pack "!H" : !=network byte order, H=unsigned short
        header = struct.pack("!H", len(encoded_msg))
        msg = header + encoded_msg
        sock.sendall(msg);

    def setup_SSL_context(self):
        #uporabi samo TLS, ne SSL
        context = ssl.SSLContext(ssl.PROTOCOL_TLSv1)
        # certifikat je obvezen
        context.verify_mode = ssl.CERT_REQUIRED
        #nalozi svoje certifikate
        context.load_cert_chain(certfile=SERVERPUBLICCERTPATH, keyfile=SERVERPRIVATECERTPATH)

        # nalozi certifikate CAjev, ki jim zaupas (samopodp. cert.=svoja CA!)
        context.load_verify_locations(TRUSTEDCERTPATH)
        # nastavi SSL CipherSuites (nacin kriptiranja)
        context.set_ciphers('AES128-SHA')

        return context

    def serve_forever(self):
        my_ssl_ctx = self.setup_SSL_context()

        ss = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        ss.bind(self.server_address)
        # max st hkratnih uporabnikov
        ss.listen(1)

        print("Zaganjam streznik...")

        #poslusaj v zanki, dokler se odjemalec ne priklopi
        while 1:
            try:
                conn, addr = ss.accept()
                conn = my_ssl_ctx.wrap_socket(conn, server_side=True)

                #ime uporabnika dobimo iz certifikata
                cert = conn.getpeercert()
                for sub in cert['subject']:
                    for key, value in sub:
                        # v commonName je ime uporabnika
                        if key == 'commonName':
                            self.USER = value

                if(self.USER == ""):
                    print("Ni mogoce dobiti imena uporpabnika iz certifikata!")
                else:
                    print('Connected by', addr,'User:',self.USER)

                    #poslusaj v zanki, dokler odjemalec ne zapre vtica
                    while 1:
                        try:
                            data = self.receive_msg(conn)
                            if not data: break
                            # pridobimo zahtevo
                            zahteva = xml.dom.minidom.parseString(data)

                            #polepsamo in izpisemo zahtevo
                            print(zahteva.toprettyxml("\t","\n"))

                            # excute
                            odgovor = self.dolociZahtevoInIzvedi(zahteva)

                            # polepsamo in izpisemo odgovor
                            print(odgovor.toprettyxml("\t","\n"))

                            # pošljemo
                            self.send_msg(conn,odgovor.toxml())

                            # označimo konec
                            print("------------------------------------")
                        except RuntimeError as e:
                            print(e)
                            #import traceback;traceback.print_exc()
                            break

                conn.close()
            except KeyboardInterrupt:
                break
        ss.close()

    # funkcija vrne trenutno stanje trenutnega uporabnika
    def pridobiTrenutnoStanje(self):
        if(self.USER in self.STANJA):
            return self.STANJA[self.USER]
        else:
            self.STANJA[self.USER] = 0
            return self.STANJA[self.USER]

    # Funkcija ki prebere zahtevo in ugotovi kaksna je, jo klice in vrne odgovor
    def dolociZahtevoInIzvedi(self, zahteva):

        # prebere atribut zahteve
        tipAtribut = zahteva.getElementsByTagName("Zahteva")[0].attributes['tip'].value

        if tipAtribut == 'Dvig':
            return self.dvigniDenar(zahteva)
        elif tipAtribut == 'Polog':
            return self.poloziDenar(zahteva)
        elif tipAtribut == 'Stanje':
            return self.pridobiStanje(zahteva)

    # Funkcija ki prebere zahtevo za dvig in dvigne denar
    def dvigniDenar(self, zahteva):
        # prebere zahtevo
        idRacuna = zahteva.getElementsByTagName("IdRacuna")[0].firstChild.nodeValue
        vsota = int(zahteva.getElementsByTagName("Vsota")[0].firstChild.nodeValue)

        # spremenljivka ce je dvig mozen
        uspesno = 1

        # preveri če je vsota pozitivna
        if vsota < 0:
            print("Vsota za dvig more biti večja od 0.")
            uspesno = 0
        else:
            # preveri če je dovolj denarja za dvig
            # za zdaj se preveri neko izmisljeno stanje
            if vsota > self.pridobiTrenutnoStanje():
                uspesno = 0
            else:
                uspesno = 1

            # odsteje denar ce lahko
            # za zdaj neka izmisljena st.
            if uspesno:
                self.STANJA[self.USER]  = self.pridobiTrenutnoStanje() - vsota

            # izpise razultat
            if uspesno:
                print("Uspesno ste dvignili " + str(vsota) + " EUR")
            else:
                print("Ni dovolj denarja na računu za dvig " + str(vsota) + " EUR")


        # zgradi odgovor in ga vrne
        impl = xml.dom.minidom.getDOMImplementation()

        odgovorDoc = impl.createDocument(None, 'Odgovor', None)
        odgovorElement = odgovorDoc.documentElement

        rezultatElement = odgovorDoc.createElement('Rezultat')
        odgovorElement.appendChild(rezultatElement)
        if uspesno:
            rezultatElement.appendChild(odgovorDoc.createTextNode('Uspesno'))
        else:
            rezultatElement.appendChild(odgovorDoc.createTextNode('Neuspesno'))


        trenutnoStanjeElement = odgovorDoc.createElement('TrenutnoStanje')
        odgovorElement.appendChild(trenutnoStanjeElement)
        trenutnoStanjeElement.appendChild(odgovorDoc.createTextNode(str(self.pridobiTrenutnoStanje())))


        return odgovorDoc

    # Funkcija ki prebere zahtevo za polog in polozi denar
    def poloziDenar(self, zahteva):
        # prebere zahtevo
        idRacuna = zahteva.getElementsByTagName("IdRacuna")[0].firstChild.nodeValue
        vsota = int(zahteva.getElementsByTagName("Vsota")[0].firstChild.nodeValue)

        # spremenljivka ce je dvig mozen
        uspesno = 1

        # preveri če je vsota pozitivna
        if vsota < 0:
            print("Vsota za polog more biti večja od 0.")
            uspesno = 0
        else:
            # polozi denar
            self.STANJA[self.USER] = self.pridobiTrenutnoStanje() + vsota
            uspesno = 1
            print("Uspesno ste polozili " + str(vsota) + " EUR")


        # zgradi odgovor in ga vrne
        impl = xml.dom.minidom.getDOMImplementation()

        odgovorDoc = impl.createDocument(None, 'Odgovor', None)
        odgovorElement = odgovorDoc.documentElement

        rezultatElement = odgovorDoc.createElement('Rezultat')
        odgovorElement.appendChild(rezultatElement)
        if uspesno:
            rezultatElement.appendChild(odgovorDoc.createTextNode('Uspesno'))
        else:
            rezultatElement.appendChild(odgovorDoc.createTextNode('Neuspesno'))


        trenutnoStanjeElement = odgovorDoc.createElement('TrenutnoStanje')
        odgovorElement.appendChild(trenutnoStanjeElement)
        trenutnoStanjeElement.appendChild(odgovorDoc.createTextNode(str(self.pridobiTrenutnoStanje())))


        return odgovorDoc

    # Funkcija ki prebere zahtevo za prikaz stanja in vrne stanje
    def pridobiStanje(self, zahteva):
        # prebere zahtevo
        idRacuna = zahteva.getElementsByTagName("IdRacuna")[0].firstChild.nodeValue

        # zgradi odgovor in ga vrne
        impl = xml.dom.minidom.getDOMImplementation()

        odgovorDoc = impl.createDocument(None, 'Odgovor', None)
        odgovorElement = odgovorDoc.documentElement

        rezultatElement = odgovorDoc.createElement('Rezultat')
        odgovorElement.appendChild(rezultatElement)

        rezultatElement.appendChild(odgovorDoc.createTextNode('Uspesno'))

        trenutnoStanjeElement = odgovorDoc.createElement('TrenutnoStanje')
        odgovorElement.appendChild(trenutnoStanjeElement)
        trenutnoStanjeElement.appendChild(odgovorDoc.createTextNode(str(self.pridobiTrenutnoStanje())))


        return odgovorDoc

if __name__ == '__main__':
    try:
        ip  = sys.argv[1]
        port = sys.argv[2]
        TRUSTEDCERTPATH = sys.argv[3]
        SERVERPRIVATECERTPATH= sys.argv[4]
        SERVERPUBLICCERTPATH= sys.argv[5]
    except:
        print ("Argumente je potrebno : IP Vrata PotDoCertifikatovClientov PotDoServerPrivatnegaCert PotDoServerJavnegaCert")
        sys.exit(2)

    server = Server((ip,int(port)))
    server.serve_forever()