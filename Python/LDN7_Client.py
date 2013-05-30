#coding:UTF-8
import ssl
import struct
import socket
import sys
import xml.dom.minidom


class Client:
    # maksimalna dolzina XMLja je 16 bit ushort (2 Bytes) = max. 64KB dolgo sporocilo
    HEADER_LENGTH = 2
    # pot server certifikata
    SERVERCERTPATH=""
    # pot do privatnega kljuca
    CLIENTPRIVATECERTPATH=""
    # pot do javnega kljuca
    CLIENTPUBLICCERTPATH=""

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
        context.load_cert_chain(certfile=CLIENTPUBLICCERTPATH, keyfile=CLIENTPRIVATECERTPATH)
        # nalozi certifikate CAjev, ki jim zaupas (samopodp. cert.=svoja CA!)
        context.load_verify_locations(SERVERCERTPATH)
        # nastavi SSL CipherSuites (nacin kriptiranja)
        context.set_ciphers('AES128-SHA')

        return context

        # funkcija ki generira xml dviga
    def kreirajDvig(self, value):
        impl = xml.dom.minidom.getDOMImplementation()

        zahtevaDoc = impl.createDocument(None, 'Zahteva', None)
        zahtevaElement = zahtevaDoc.documentElement
        zahtevaElement.setAttribute('tip','Dvig')

        idRacunaElement = zahtevaDoc.createElement('IdRacuna')
        zahtevaElement.appendChild(idRacunaElement)
        idRacunaElement.appendChild(zahtevaDoc.createTextNode('00001'))

        vsotaElement = zahtevaDoc.createElement('Vsota')
        zahtevaElement.appendChild(vsotaElement)
        vsotaElement.appendChild(zahtevaDoc.createTextNode(value))

        return zahtevaDoc

    # funkcija ki generira xml pologa
    def kreirajPolog(self, value):
        impl = xml.dom.minidom.getDOMImplementation()

        zahtevaDoc = impl.createDocument(None, 'Zahteva', None)
        zahtevaElement = zahtevaDoc.documentElement
        zahtevaElement.setAttribute('tip','Polog')

        idRacunaElement = zahtevaDoc.createElement('IdRacuna')
        zahtevaElement.appendChild(idRacunaElement)
        idRacunaElement.appendChild(zahtevaDoc.createTextNode('00001'))

        vsotaElement = zahtevaDoc.createElement('Vsota')
        zahtevaElement.appendChild(vsotaElement)
        vsotaElement.appendChild(zahtevaDoc.createTextNode(value))

        return zahtevaDoc

    # funkcija ki generira xml za pridobitev trenutnega stanja
    def kreirajStanje(self):
        impl = xml.dom.minidom.getDOMImplementation()

        zahtevaDoc = impl.createDocument(None, 'Zahteva', None)
        zahtevaElement = zahtevaDoc.documentElement
        zahtevaElement.setAttribute('tip','Stanje')

        idRacunaElement = zahtevaDoc.createElement('IdRacuna')
        zahtevaElement.appendChild(idRacunaElement)
        idRacunaElement.appendChild(zahtevaDoc.createTextNode('00001'))

        return zahtevaDoc

    def run(self):
        my_ssl_ctx = self.setup_SSL_context()

        s = my_ssl_ctx.wrap_socket(socket.socket(socket.AF_INET, socket.SOCK_STREAM))
        s.connect(self.server_address)

        print("Bankomat zagnan...")
        print("Polog x EUR : P x, Dvig x EUR : D x, Stanje : S")

        nadaljuj = 1

        while nadaljuj:
            # zahteva ki se bo generirala
            zahteva = None

            # dobimo input
            inputValue = input("Ukaz:").split(' ')

            # polog
            if((inputValue[0] == "P" or inputValue[0] == "p") and inputValue.__len__() == 2):
                zahteva = self.kreirajPolog(inputValue[1])
            # dvig
            elif((inputValue[0] == "D" or inputValue[0] == "d") and inputValue.__len__() == 2):
                zahteva = self.kreirajDvig(inputValue[1])
            # stanje
            elif(inputValue[0] == "S" or inputValue[0] == "s"):
                zahteva = self.kreirajStanje()

            else:
                print("Napacen ukaz.")
                continue

            # izpisemo zahtevo
            print(zahteva.toprettyxml("\t","\n"))

            # pošljemo
            self.send_msg(s, zahteva.toxml())

            # dobimo odgovor
            odgovor = xml.dom.minidom.parseString(self.receive_msg(s))

            # izpisemo odgovor
            print(odgovor.toprettyxml("\t","\n"))

            # označimo konec
            print("------------------------------------")

        s.close()

if __name__ == '__main__':
    try:
        ip  = sys.argv[1]
        port = sys.argv[2]
        SERVERCERTPATH = sys.argv[3]
        CLIENTPRIVATECERTPATH= sys.argv[4]
        CLIENTPUBLICCERTPATH= sys.argv[5]
    except:
        print ("Argumente je potrebno : IP Vrata PotServerCert PotClientPrivateCert PotClientPublicCert")
        sys.exit(2)

    client = Client((ip,int(port)))
    client.run()