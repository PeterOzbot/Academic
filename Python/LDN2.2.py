__author__ = 'Peter Ozbot'
#coding:UTF-8
import xml.dom.minidom

# Funkcija ki prebere zahtevo za dvig in dvigne denar
def dvigniDenar(zahteva):
    # prebere zahtevo
    idRacuna = zahteva.getElementsByTagName("IdRacuna")[0].firstChild.nodeValue
    vsota = int(zahteva.getElementsByTagName("Vsota")[0].firstChild.nodeValue)

    # spremenljivka ce je dvig mozen
    uspesno = 1

    # sremenjivka za trenutno stanje
    # za zdaj izmisljeno
    trenutnoStanje = 100

    # preveri če je vsota pozitivna
    if vsota < 0:
        print("Vsota za dvig more biti večja od 0.")
        uspesno = 0
    else:
        # preveri če je dovolj denarja za dvig
        # za zdaj se preveri neko izmisljeno stanje
        if vsota > trenutnoStanje:
            uspesno = 0
        else:
            uspesno = 1

        # odsteje denar ce lahko
        # za zdaj neka izmisljena st.
        if uspesno:
            trenutnoStanje = trenutnoStanje - vsota

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


    trenutnoStanjeElement = odgovorDoc.createElement('Trenutno stanje')
    odgovorElement.appendChild(trenutnoStanjeElement)
    trenutnoStanjeElement.appendChild(odgovorDoc.createTextNode(str(trenutnoStanje)))


    return odgovorDoc

# Funkcija ki prebere zahtevo za polog in polozi denar
def poloziDenar(zahteva):
    # prebere zahtevo
    idRacuna = zahteva.getElementsByTagName("IdRacuna")[0].firstChild.nodeValue
    vsota = int(zahteva.getElementsByTagName("Vsota")[0].firstChild.nodeValue)

    # spremenljivka ce je dvig mozen
    uspesno = 1

    # sremenjivka za trenutno stanje
    # za zdaj izmisljeno
    trenutnoStanje = 100

    # preveri če je vsota pozitivna
    if vsota < 0:
        print("Vsota za polog more biti večja od 0.")
        uspesno = 0
    else:
        # polozi denar
        trenutnoStanje = trenutnoStanje + vsota
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


    trenutnoStanjeElement = odgovorDoc.createElement('Trenutno stanje')
    odgovorElement.appendChild(trenutnoStanjeElement)
    trenutnoStanjeElement.appendChild(odgovorDoc.createTextNode(str(trenutnoStanje)))


    return odgovorDoc

# Funkcija ki prebere zahtevo za prikaz stanja in vrne stanje
def pridobiStanje(zahteva):
    # prebere zahtevo
    idRacuna = zahteva.getElementsByTagName("IdRacuna")[0].firstChild.nodeValue

    # sremenjivka za trenutno stanje
    # za zdaj izmisljeno
    trenutnoStanje = 100

    # zgradi odgovor in ga vrne
    impl = xml.dom.minidom.getDOMImplementation()

    odgovorDoc = impl.createDocument(None, 'Odgovor', None)
    odgovorElement = odgovorDoc.documentElement

    rezultatElement = odgovorDoc.createElement('Rezultat')
    odgovorElement.appendChild(rezultatElement)

    rezultatElement.appendChild(odgovorDoc.createTextNode('Uspesno'))


    trenutnoStanjeElement = odgovorDoc.createElement('Trenutno stanje')
    odgovorElement.appendChild(trenutnoStanjeElement)
    trenutnoStanjeElement.appendChild(odgovorDoc.createTextNode(str(trenutnoStanje)))


    return odgovorDoc

# Funkcija ki prebere zahtevo in ugotovi kaksna je, jo klice in vrne odgovor
def dolociZahtevoInIzvedi(zahteva):

    # prebere atribut zahteve
    tipAtribut = zahteva.getElementsByTagName("Zahteva")[0].attributes['tip'].value

    if tipAtribut == 'Dvig':
        return dvigniDenar(zahteva)
    elif tipAtribut == 'Polog':
        return poloziDenar(zahteva)
    elif tipAtribut == 'Stanje':
        return pridobiStanje(zahteva)

# funkcija ki generira primer dviga
def kreirajPrimerDvig():
    impl = xml.dom.minidom.getDOMImplementation()

    zahtevaDoc = impl.createDocument(None, 'Zahteva', None)
    zahtevaElement = zahtevaDoc.documentElement
    zahtevaElement.setAttribute('tip','Dvig')

    idRacunaElement = zahtevaDoc.createElement('IdRacuna')
    zahtevaElement.appendChild(idRacunaElement)
    idRacunaElement.appendChild(zahtevaDoc.createTextNode('00001'))

    vsotaElement = zahtevaDoc.createElement('Vsota')
    zahtevaElement.appendChild(vsotaElement)
    vsotaElement.appendChild(zahtevaDoc.createTextNode('80'))

    return zahtevaDoc

# funkcija ki generira primer pologa
def kreirajPrimerPolog():
    impl = xml.dom.minidom.getDOMImplementation()

    zahtevaDoc = impl.createDocument(None, 'Zahteva', None)
    zahtevaElement = zahtevaDoc.documentElement
    zahtevaElement.setAttribute('tip','Polog')

    idRacunaElement = zahtevaDoc.createElement('IdRacuna')
    zahtevaElement.appendChild(idRacunaElement)
    idRacunaElement.appendChild(zahtevaDoc.createTextNode('00001'))

    vsotaElement = zahtevaDoc.createElement('Vsota')
    zahtevaElement.appendChild(vsotaElement)
    vsotaElement.appendChild(zahtevaDoc.createTextNode('180'))

    return zahtevaDoc

# funkcija ki generira primer za pridobitev trenutnega stanja
def kreirajPrimerStanje():
    impl = xml.dom.minidom.getDOMImplementation()

    zahtevaDoc = impl.createDocument(None, 'Zahteva', None)
    zahtevaElement = zahtevaDoc.documentElement
    zahtevaElement.setAttribute('tip','Stanje')

    idRacunaElement = zahtevaDoc.createElement('IdRacuna')
    zahtevaElement.appendChild(idRacunaElement)
    idRacunaElement.appendChild(zahtevaDoc.createTextNode('00001'))

    return zahtevaDoc

if __name__ == '__main__':
    # generiramo primer zahteve za dvig
    zahteva = kreirajPrimerDvig()

    # polepsamo in izpisemo primer zahteve za dvig
    print(zahteva.toprettyxml("\t","\n"))

    # izvedemo zahtevo
    odgovor = dolociZahtevoInIzvedi(zahteva)

    # polepsamo in izpisemo odgovor
    print(odgovor.toprettyxml("\t","\n"))


    # generiramo primer zahteve za polog
    zahteva = kreirajPrimerPolog()

    # polepsamo in izpisemo primer zahteve za polog
    print(zahteva.toprettyxml("\t","\n"))

    # izvedemo zahtevo
    odgovor = dolociZahtevoInIzvedi(zahteva)

    # polepsamo in izpisemo odgovor
    print(odgovor.toprettyxml("\t","\n"))




    # generiramo primer zahteve za pridobitev stanja
    zahteva = kreirajPrimerStanje()

    # polepsamo in izpisemo primer zahteve za pridobitev stanja
    print(zahteva.toprettyxml("\t","\n"))

    # izvedemo zahtevo
    odgovor = dolociZahtevoInIzvedi(zahteva)

    # polepsamo in izpisemo odgovor
    print(odgovor.toprettyxml("\t","\n"))
