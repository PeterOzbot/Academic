package compiler.lexanal;

import java.awt.List;
import java.io.*;
import java.util.ArrayList;

import javax.jws.Oneway;

import compiler.Report;

/** Leksikalni analizator. */
public class LexAnal {
	// za stet vrstice in stolpce
	private final int START_COLUMN_INDEX = 0;
	private final int START_LINE_INDEX = 1;

	// regexi
	private final String IDENTIFIER_REGEX = "[A-Za-z_][A-Za-z0-9_]*";
	private final String NUMBER_REGEX = "[[0-9]+";
	private final String FLOAT_REGEX = "[0-9]+\\.[0-9]+([Ee][+-]?[0-9]+)?";
	private final String STRING_REGEX = "\\\"([^\\\\\\\"]|\\\\[\\\\\\\"])*\\\"";

	// za branje datoteke
	private BufferedReader _bufferedReader = null;
	private String _programName = "";
	private int _currentLineIndex = START_LINE_INDEX;
	private int _currentColumnIndex = START_COLUMN_INDEX;
	// trenuten simbol
	private StringBuilder _simbol = new StringBuilder();
	// trenutno se gradi
	private TipSimbola _trenutenTip = TipSimbola.UNDEFINED;
	// oznacuje da pri naslednji zahtevi simbola je simbol ze pripravljen
	private Boolean _symbolInBuffer = false;

	/** Ustvari nov leksikalni analizator. */
	public LexAnal() {
	}

	/**
	 * Odpre datoteko z izvorno kodo programa.
	 * 
	 * @param programName
	 *            ime datoteke z izvorno kodo programa.
	 * @throws IOException
	 *             Ce datoteke z izvorno kodo programa ni mogoce odpreti.
	 */
	public void openSourceFile(String programName) throws IOException {

		// ce ni null je bila metoda ze klicana, null postane ko se zapre in
		// konca branje
		// null pomeni da je treba inicializirati
		if (_bufferedReader == null) {

			// shrani ime datoteke
			_programName = programName;

			// nastavi index vrstice na zacetno
			_currentLineIndex = START_LINE_INDEX;

			// inicializacija buffered reader
			FileInputStream fileInputStream = new FileInputStream(programName);

			DataInputStream dataInputStream = new DataInputStream(
					fileInputStream);

			_bufferedReader = new BufferedReader(new InputStreamReader(
					dataInputStream));
		}
	}

	/**
	 * Zapre datoteko z izvorno kodo programa.
	 * 
	 * @throws IOException
	 *             Ce datoteke z izvorno kodo programa ni mogoce zapreti.
	 */
	public void closeSourceFile() throws IOException {
		// zapre in konca branje datoteke
		if (_bufferedReader != null)
			_bufferedReader.close();

		_bufferedReader = null;
	}

	/**
	 * Vrne naslednji osnovni simbol.
	 * 
	 * @return Naslednji osnovni simbol ali <code>null</code> ob koncu datoteke.
	 * @throws IOException
	 *             Ce je prislo do napake pri branju vhodne datoteke.
	 */
	public Symbol getNextSymbol() throws IOException {
		// ce je null je potrebno znova odpreti in zaceti branje
		if (_bufferedReader != null) {

			// flag da je ze simbol zgrajen
			if (_symbolInBuffer) {
				_symbolInBuffer = false;
				Symbol symbol = ZgradiSimbol(_simbol.toString());
				_simbol = new StringBuilder();
				return symbol;
			}

			// ce je prvi znak " pomeni da je zacetek string konstante in gre
			// samo cez in vrne
			if (_simbol.length() == 1
					&& _simbol.toString().toCharArray()[0] == '"') {
				// gre cez cel string in vrne simbol
				Symbol symbol = ZgradiSimbol("\"" + Niz(_bufferedReader));
				_simbol = new StringBuilder();
				return symbol;
			}

			while (true) {
				// prebere naslednji znak
				int prebranaVrednost = _bufferedReader.read();

				if (prebranaVrednost == -1) {
					// konec datoteke ce se gradi simbol ga vrne in zakljuci
					if (_simbol.length() > 0) {
						Symbol symbol = ZgradiSimbol(_simbol.toString());
						_simbol = new StringBuilder();
						return symbol;
					}
					break;
				}

				// pretvori v znak
				char znak = (char) prebranaVrednost;

				// pristeje stevec vrstice
				_currentColumnIndex++;

				// preveri prazne znake
				SubResult subResult = CheckWhiteSpaces(znak, _bufferedReader);
				if (subResult.Symbol != null) {
					return subResult.Symbol;
				}
				if (subResult.PreskociZnak) {
					continue;
				}

				// preveri ce je znak za novo vrstico
				if (znak == '\r') {
					continue;
				}
				// ce je znak za novo vrstico poveca stevec vrstice in
				// resetira stevec stolpca
				if (znak == '\n') {
					if (_simbol.length() > 0) {
						// konec simbola ko pri do nove vrstice
						Symbol symbol = ZgradiSimbol(_simbol.toString());
						_simbol = new StringBuilder();
						return symbol;
					}

					_currentLineIndex++;
					_currentColumnIndex = START_COLUMN_INDEX;

					// nadaljuje z naslednjim znakom
					continue;
				}

				// string constanta
				if (znak == '"') {
					// potrebno je zakljucit trenuten simbol
					if (_simbol.length() > 0) {
						Symbol symbol = ZgradiSimbol(_simbol.toString());
						_simbol = new StringBuilder();
						// dodamo znak za novi simbol
						_simbol.append(znak);
						return symbol;
					}

					// gre cez cel string in vrne simbol
					Symbol symbol = ZgradiSimbol("\"" + Niz(_bufferedReader));
					_simbol = new StringBuilder();
					return symbol;
				}

				// ali je ze kaksen znak v bufferju
				// ce je se preverja kaj se bo zgodilo naprej
				if (_simbol.length() > 0) {

					// ce gradi string
					if(_trenutenTip == TipSimbola.STRING)
					{
						int a = 1;
					}
					// ce se gradi stevilo
					if (_trenutenTip == TipSimbola.STEVILO) {
						// ce je stevilo se doda in nadaljuje z novim znakom
						if (Character.isDigit(znak)) {
							_simbol.append(znak);
							continue;
						} else {
							// ce je znak pika je potrebno pretvori v float
							// stevilo
							if (znak == '.') {
								_trenutenTip = TipSimbola.FLOAT;
								_simbol.append(znak);
								continue;
							}

							// ce je crka je napaka
							if (Character.isAlphabetic(znak)) {
								Report.error("Crka med grajenjem stevila",
										new Position(_programName,
												_currentLineIndex,
												_currentColumnIndex,
												_currentLineIndex,
												_currentColumnIndex), 1);
							}

							// preveri ce je naslednji znak enojen operand
							// ce je je potrebno vrnit dosedanji simbol in
							// shrani
							// trenuten znak za naslednji simbol
							if (CheckIfOneCharOperand(znak)) {
								Symbol symbol = ZgradiSimbol(_simbol.toString());
								_simbol = new StringBuilder();
								_simbol.append(znak);
								_symbolInBuffer = true;
								return symbol;
							}
							
							// preveri ce je mogoce operand iz dveh znakov
							if(IsTwoCharOperand(znak))
							{
								// ce je vrne trenuten simbol
								Symbol symbol = ZgradiSimbol(_simbol.toString());
								// doda znak za naprej
								_simbol = new StringBuilder();
								_simbol.append(znak);
								// oznaci da bo operand
								_trenutenTip = TipSimbola.OPERAND;
								
								return symbol;
							}
							
							
						}
					}

					// ce se gradi stevilo - FLOAT
					// sem pride samo v primeru da se je zgradilo simbol do
					// x=stevka -> xxx.
					if (_trenutenTip == TipSimbola.FLOAT) {
						// ce je prejsnji znak . potem je lahko samo stevilo
						if (GetLastChar(_simbol) == '.') {
							if (Character.isDigit(znak)) {
								_simbol.append(znak);
								continue;
							} else {
								// po piki obvezno stevilo, ce ni je napaka
								Report.error("Napacno float stevilo", new Position(
										_programName, _currentLineIndex,
										_currentColumnIndex, _currentLineIndex,
										_currentColumnIndex), 1);
							}
						}

						// ce je prejsnji znak E ali e je naslednji lahko samo +
						// ali -
						if (GetLastChar(_simbol) == 'e'
								|| GetLastChar(_simbol) == 'E') {
							if (znak == '+' || znak == '-') {
								_simbol.append(znak);
								continue;
							} else {
								// po E ali e je lahko samo predznak
								Report.error("Napacno float stevilo", new Position(
										_programName, _currentLineIndex,
										_currentColumnIndex, _currentLineIndex,
										_currentColumnIndex), 1);
							}
						}

						// ce je prejsnji znak + ali - je naslednji lahko samo
						// stevilka
						if (GetLastChar(_simbol) == '+'
								|| GetLastChar(_simbol) == '-') {
							if (Character.isDigit(znak)) {
								_simbol.append(znak);
								continue;
							} else {
								// samo stevilka je lahko za + ali -
								Report.error("Napacno float stevilo", new Position(
										_programName, _currentLineIndex,
										_currentColumnIndex, _currentLineIndex,
										_currentColumnIndex), 1);
							}
						}

						// ce znak ni stevilo, je lahko samo E ali e ali + ali -
						// ce je kaj drugega in float ni dokoncan je napaka
						if (!Character.isDigit(znak)) {
							if (znak == 'e' || znak == 'E' || znak == '+'
									|| znak == '-') {

								// e ali E se doda ce se nista bila dodana
								if (znak == 'e' || znak == 'E') {
									// ce vsebuje lahko pomeni da je konec float
									// ali napako
									if (_simbol.toString().contains("e")
											|| _simbol.toString().contains("E")) {
										// ce je do zdaj ze zlozen float se ga
										// vrne
										// preverit ce je float koncan
										if (_simbol.toString().matches(
												FLOAT_REGEX)) {
											// vrni float simbol
											Symbol symbol = ZgradiSimbol(_simbol
													.toString());
											_simbol = new StringBuilder();
											// doda trenuten znak za zacetek
											// novega
											// naslednjega
											_simbol.append(znak);
											// nastavi da se ne gradi vec float
											SetTipSimbola(znak);
											// vrne
											return symbol;
										} else {
											// ce ni zlozen in je znak e ali E
											// je napaka
											Report.error("Napacno float stevilo", new Position(
													_programName, _currentLineIndex,
													_currentColumnIndex, _currentLineIndex,
													_currentColumnIndex), 1);
										}
									} else {
										// ce ne vsebuje e ali E se doda
										_simbol.append(znak);
										continue;

									}
								}

								// + ali - se doda ce se nista bila dodana
								if (znak == '+' || znak == '-') {
									// ce vsebuje lahko pomeni da je konec float
									// ali napako
									if (_simbol.toString().contains("+")
											|| _simbol.toString().contains("-")) {
										// ce je do zdaj ze zlozen float se ga
										// vrne
										// preverit ce je float koncan
										if (_simbol.toString().matches(
												FLOAT_REGEX)) {
											// vrni float simbol
											Symbol symbol = ZgradiSimbol(_simbol
													.toString());
											_simbol = new StringBuilder();
											// doda trenuten znak za zacetek
											// novega
											// naslednjega
											_simbol.append(znak);
											// nastavi da se ne gradi vec float
											SetTipSimbola(znak);
											// vrne
											return symbol;
										} else {
											// ce ni zlozen in je znak + ali -
											// je napaka
											Report.error("Napacno float stevilo", new Position(
													_programName, _currentLineIndex,
													_currentColumnIndex, _currentLineIndex,
													_currentColumnIndex), 1);
										}
									} else {
										// ce ne vsebuje + ali - se doda
										_simbol.append(znak);
										continue;

									}
								}

							} else {
								// preverit ce je float koncan
								if (_simbol.toString().matches(FLOAT_REGEX)) {
									// vrni float simbol
									Symbol symbol = ZgradiSimbol(_simbol
											.toString());
									_simbol = new StringBuilder();
									// doda trenuten znak za zacetek novega
									// naslednjega
									_simbol.append(znak);
									// nastavi da se ne gradi vec float
									SetTipSimbola(znak);
									// vrne
									return symbol;
								} else {
									Report.error("Napacno float stevilo", new Position(
											_programName, _currentLineIndex,
											_currentColumnIndex, _currentLineIndex,
											_currentColumnIndex), 1);
								}
							}
						} else {
							// ce je znak stevilka in od prejsnjih pogojev ni
							// prejsnji .,+,-,e,E
							// se ga doda in nadaljuje
							_simbol.append(znak);
							continue;
						}
					}

					// ce se gradi beseda
					if (_trenutenTip == TipSimbola.BESEDA) {
						// ce je trenuten znak crka, stevka ali podcrtaj
						// se doda besedi in nadaljuje
						if (Character.isLetter(znak) || Character.isDigit(znak)
								|| znak == '_') {
							_simbol.append(znak);
							continue;
						} else {

							// ce je naslednji znak enojen operand
							if (CheckIfOneCharOperand(znak)) {
								Symbol symbol = ZgradiSimbol(_simbol.toString());
								_simbol = new StringBuilder();
								_simbol.append(znak);
								_symbolInBuffer = true;
								return symbol;
							}

							// ce ni vrne dosedanji simbol
							Symbol symbol = ZgradiSimbol(_simbol.toString());
							_simbol = new StringBuilder();
							// doda trenuten znak za zacetek novega
							// naslednjega
							_simbol.append(znak);
							// nastavi da se ne gradi vec besede
							SetTipSimbola(znak);
							// vrne
							return symbol;
						}
					}

					// ce se gradi operand
					if (_trenutenTip == TipSimbola.OPERAND) {
						// ce je drugi del operanda vrnemo simbol
						if (CheckIfTwoCharOperand(GetLastChar(_simbol), znak)) {
							// dodamo noter
							_simbol.append(znak);
							// vrnemo
							Symbol symbol = ZgradiSimbol(_simbol.toString());
							_simbol = new StringBuilder();
							// vrne
							return symbol;
						}

						// ce je stevilo ali crka
						if (Character.isAlphabetic(znak)
								|| Character.isDigit(znak)) {
							// pomeni da je enojen operand
							Symbol symbol = ZgradiSimbol(_simbol.toString());
							_simbol = new StringBuilder();
							// doda trenuten znak za zacetek novega
							// naslednjega
							_simbol.append(znak);
							// nastavi da se ne gradi vec operanda
							SetTipSimbola(znak);
							// vrne
							return symbol;
						}

						// ce je enojen operand primer =-1
						if (CheckIfOneCharOperand(znak)) {
							Symbol symbol = ZgradiSimbol(_simbol.toString());
							_simbol = new StringBuilder();
							_simbol.append(znak);
							_symbolInBuffer = true;
							return symbol;
						}

						// ce ni dvojni operand in naslednji znak ni crka ali
						// stevilka je napaka
						Report.error("Napacen operand", new Position(
								_programName, _currentLineIndex,
								_currentColumnIndex, _currentLineIndex,
								_currentColumnIndex), 1);
					}

				} else
				// ce ni se nobenega ga doda v buffer
				{
					// za mozne simbole z enim znakom kar vrne
					if (CheckIfOneCharOperand(znak)) {

						Symbol symbol = ZgradiSimbol("" + znak);
						_simbol = new StringBuilder();
						return symbol;
					}

					// nastavi kaj se bo gradilo
					SetTipSimbola(znak);

					// ce je mozno da imajo vec znakov doda v buffer
					_simbol.append(znak);
				}
			}
		}

		// konec , vrne null
		return null;
	}

	private SubResult CheckWhiteSpaces(char znak, BufferedReader bufferedReader)
			throws IOException {
		// preveri ce je tab
		// ce je zakjuci prejsnji simbol in dodal k dolzini 3 znake
		// (1 je bil pristet ze)
		if (znak == '\t') {
			_currentColumnIndex = _currentColumnIndex + 3;

			if (_simbol.length() > 0) {
				Symbol symbol = ZgradiSimbol(_simbol.toString());
				_simbol = new StringBuilder();
				return new SubResult(symbol, true);
			}

			return new SubResult(null, true);
		}

		// preveri ce je presledek
		// ce je zakjuci prejsnji simbol
		if (znak == ' ') {
			if (_simbol.length() > 0) {
				Symbol symbol = ZgradiSimbol(_simbol.toString());
				_simbol = new StringBuilder();
				return new SubResult(symbol, true);
			}

			return new SubResult(null, true);
		}

		// preveri ce je zacetek komentarja
		// ce je zakljuci prejsnji simbol
		// in gre cez cel komentar - do konca vrstice
		if (znak == '#') {
			// prvo zgradi simbol
			Symbol symbol = null;
			if (_simbol.length() > 0) {
				symbol = ZgradiSimbol(_simbol.toString());
				_simbol = new StringBuilder();
			}

			// gre cez cel komentar
			Komentar(bufferedReader);

			// vrstica se poveca - komentar pusti v naslednji vrstici
			_currentLineIndex++;
			// reseta st. vrstice
			_currentColumnIndex = START_COLUMN_INDEX;

			// vrne null ce ni simbola ali pa simbol
			return new SubResult(symbol, true);
		}

		return new SubResult(null, false);
	}

	// gre cez vse znake ki se stejejo za komentar
	// vrne true ce je konec datoteke po komentarju
	private Boolean Komentar(BufferedReader bufferedReader) throws IOException {

		StringBuilder komentar = new StringBuilder();

		while (true) {
			// prebere naslednji znak
			int prebranaVrednost = bufferedReader.read();

			// sporoci da je prislo do konca datoteke
			if (prebranaVrednost == -1) {
				ObvestiOKomentarju(komentar.toString());
				return true;
			}

			// pretvori prebrano vrednost v znak
			char znak = (char) prebranaVrednost;

			// preskocimo znak za novo vrstico
			if (znak == '\r') {
				continue;
			}

			// ce pride do znaka \n je konec vrstice
			if (znak == '\n') {
				ObvestiOKomentarju(komentar.toString());
				return false;
			}

			// v komentarju so lahko samo znaki od 32-126
			if (prebranaVrednost < 32 || prebranaVrednost > 126) {
				Report.error("Nepravilen znak v komentarju", new Position(
						_programName, _currentLineIndex, _currentColumnIndex,
						_currentLineIndex + komentar.length(),
						_currentColumnIndex), 1);
			}

			// doda znak v celoten komentar
			komentar.append(znak);
		}
	}

	private void ObvestiOKomentarju(String komentar) {

		// obvesti o komentarju
		Report.information("Komentar : " + komentar, new Position(_programName,
				_currentLineIndex, _currentColumnIndex, _currentLineIndex,
				_currentColumnIndex + komentar.length()));
	}

	private String Niz(BufferedReader bufferedReader) throws IOException {
		// cel niz
		StringBuilder niz = new StringBuilder();

		// zapomni si zacetni index vrstice in stolpca
		int startColumnIndex = _currentColumnIndex;
		int startLineIndex = _currentLineIndex;

		while (true) {

			// prebere naslednji znak
			int prebranaVrednost = bufferedReader.read();

			// ce pride do konca datoteke preden se konca niz je napaka
			if (prebranaVrednost == -1) {
				ObvestiOKomentarju(niz.toString());
				Report.error("Nedokoncan niz", new Position(_programName,
						startLineIndex, startColumnIndex, _currentLineIndex,
						_currentColumnIndex), 1);
			}

			// pretvori v znak
			char znak = (char) prebranaVrednost;

			// pristeje stevec vrstice
			_currentColumnIndex++;

			// preveri ce je nova vrstica
			if (znak == '\r')
				continue;
			if (znak == '\n') {
				Report.error("Nedokoncan niz", new Position(_programName,
						startLineIndex, startColumnIndex, _currentLineIndex,
						_currentColumnIndex), 1);

				_currentColumnIndex = START_COLUMN_INDEX;
				_currentLineIndex++;
			}

			if (znak == '\t') {
				Report.error("Tabulator v nizu", new Position(_programName,
						_currentLineIndex, _currentColumnIndex,
						_currentLineIndex, _currentColumnIndex), 1);
			}

			// preveri ce je konec niza in zakljuci
			if (znak == '"') {

				// ce je prejsnji znak \ ne pomeni konec
				if (GetLastChar(niz) != '\\') {
					niz.append(znak);
					return niz.toString();
				}
			}

			// doda niz v celoto
			niz.append(znak);
		}
	}

	private Symbol ZgradiSimbol(String simbol) {

		// zgradi pozicijo simbola
		Position position = new Position(_programName, _currentLineIndex,
				_currentColumnIndex - simbol.length(), _currentLineIndex,
				_currentColumnIndex);

		// sporoci dobro novico
		Report.information("Nov simbol: " + simbol, position);

		// TODO: pohendlat razlicne tipe
		return new Symbol(Symbol.IDENTIFIER, "" + simbol, position);
	}

	private Boolean CheckIfOneCharOperand(char znak) {
		return (znak == '+' || znak == '-' || znak == '*' || znak == '/'
				|| znak == '!' || znak == '&' || znak == '|' || znak == '('
				|| znak == ')' || znak == '[' || znak == ']' || znak == '{'
				|| znak == '}' || znak == '.' || znak == ',' || znak == ':' || znak == ';');
	}

	private Boolean CheckIfTwoCharOperand(char prviZnak, char drugiZnak) {
		switch (prviZnak) {
		case '=':
			if (drugiZnak == '=')
				return true;
			return false;
		case '<':
			if (drugiZnak == '>')
				return true;
			if (drugiZnak == '=')
				return true;
			return false;
		case '>':
			if (drugiZnak == '=')
				return true;
			return false;
		default:
			return false;
		}
	}

	private void SetTipSimbola(char znak) {

		// zapomni si kaj naj bi se gradilo
		if (Character.isLetter(znak))
			_trenutenTip = TipSimbola.BESEDA;

		if (Character.isDigit(znak)) {
			_trenutenTip = TipSimbola.STEVILO;
		}

		if (IsTwoCharOperand(znak)) {
			_trenutenTip = TipSimbola.OPERAND;
		}

		if (znak == '"') {
			_trenutenTip = TipSimbola.STRING;
		}

		if (_trenutenTip == TipSimbola.UNDEFINED) {
			Report.error("Nepravilen znak", new Position(_programName,
					_currentLineIndex, _currentColumnIndex, _currentLineIndex,
					_currentColumnIndex), 1);
		}
	}
	
	private Boolean IsTwoCharOperand(char znak)
	{
		return (znak == '=' || znak == '<' || znak == '>');
	}

	// vrne znadji znak
	private char GetLastChar(StringBuilder stringBuilder) {
		return stringBuilder.toString().toCharArray()[stringBuilder.length() - 1];
	}

	public enum TipSimbola {
		UNDEFINED, BESEDA, STEVILO, FLOAT, OPERAND, STRING
	}
}
