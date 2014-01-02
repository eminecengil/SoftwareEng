package com.example.realmancala;

import core.MancalaDB;
import ai.*;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayGameActivity extends Activity {
	static String diff;
	static String stone;
	String win;
	String draw;
	String lose;

	CBoard MainBoard; // Oyun durumunun saklanaca�� s�n�f
	P1AI player1; // Yapay zeka fonksiyonlar�n�n bulundu�u ve hangi hamlenin
					// yap�laca��n�n hesapland��� s�n�f
	CBoard copyBoard;
	boolean[] h;
	int counter;
	int boardIndex;

	final TextView[] item = new TextView[14];
	final Button[] btnItems = new Button[14];

	public void getDefaultSettings() {
		try {
			String s = "1";
			long l = Long.parseLong(s);
			MancalaDB con = new MancalaDB(PlayGameActivity.this);
			con.open();
			diff = con.getDiffLevel(l);
			stone = con.getStone(l);
			con.close();

			Toast.makeText(PlayGameActivity.this, stone + " " + diff,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {

		}
	}

	/* animasyon olu�turma */
	
	int deltaX = -96;
	int startingValue;
	int stepNumber = 0;
	int binIndex;
	int numberOfStone = 0;
	ImageView imgHand;

	public void setRightToLeftAnimation(int _stepNumber, int _startingIndex) {
		int from = _startingIndex * -96;
		int to= from-96;
//		int from =_startingIndex-96;
//		int to=-96*_stepNumber;
//		to -=96;
//		for (int i = 0; i < _stepNumber; i++) {
//			to +=-96;
//		}
		TranslateAnimation moveRightToLeft = new TranslateAnimation(from, to, 0, 0);
		moveRightToLeft.setDuration(1000);
		moveRightToLeft.setFillAfter(true);
		
		imgHand.setAnimation(moveRightToLeft);
//		_startingIndex +=1;
		if (_startingIndex >=5) {
			_startingIndex=0;
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_mancala_playgame_screen);
		getDefaultSettings();

		imgHand = (ImageView) findViewById(R.id.imgHandP1);
		/* oyun modunu al */
		final boolean gameMode = getIntent().getExtras().getBoolean("gameMode");

		if (gameMode) {
			Toast.makeText(PlayGameActivity.this, "CH", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(PlayGameActivity.this, "HH", Toast.LENGTH_SHORT)
					.show();
		}

		/* When activity loaded */
		h = new boolean[6]; // Heuristic se�imleri
		MainBoard = new CBoard();
		copyBoard = new CBoard();
		player1 = new P1AI();
		ai.CBoard.StartingStones = Integer.parseInt(stone);
		ai.CBoard.TotalStones = 12 * Integer.parseInt(stone);
		// MainBoard.Status = 2; // Oyuna ilk �nce bizim ba�lamam�z i�in.

		h[0] = false; //
		h[1] = false; //
		h[2] = false; // heuristic se�imleri resetlenir.
		h[3] = false; //
		h[4] = false; //
		h[5] = false; //

		/* ____________________________________________________________________________________ */
		/* Oyuklar ve kaleleri tan�mlama */
		item[0] = (TextView) findViewById(R.id.txt0);
		item[1] = (TextView) findViewById(R.id.txt1);
		item[2] = (TextView) findViewById(R.id.txt2);
		item[3] = (TextView) findViewById(R.id.txt3);
		item[4] = (TextView) findViewById(R.id.txt4);
		item[5] = (TextView) findViewById(R.id.txt5);
		item[6] = (TextView) findViewById(R.id.txt6);// P1 Kalesi
		item[7] = (TextView) findViewById(R.id.txt7);
		item[8] = (TextView) findViewById(R.id.txt8);
		item[9] = (TextView) findViewById(R.id.txt9);
		item[10] = (TextView) findViewById(R.id.txt10);
		item[11] = (TextView) findViewById(R.id.txt11);
		item[12] = (TextView) findViewById(R.id.txt12);
		item[13] = (TextView) findViewById(R.id.txt13);// P2 Kalesi

		/* Oyuklar ve kaleleri tan�mlama */
		btnItems[0] = (Button) findViewById(R.id.btn0);
		btnItems[1] = (Button) findViewById(R.id.btn1);
		btnItems[2] = (Button) findViewById(R.id.btn2);
		btnItems[3] = (Button) findViewById(R.id.btn3);
		btnItems[4] = (Button) findViewById(R.id.btn4);
		btnItems[5] = (Button) findViewById(R.id.btn5);
		btnItems[6] = (Button) findViewById(R.id.btn6);// P1 Kalesi
		btnItems[7] = (Button) findViewById(R.id.btn7);
		btnItems[8] = (Button) findViewById(R.id.btn8);
		btnItems[9] = (Button) findViewById(R.id.btn9);
		btnItems[10] = (Button) findViewById(R.id.btn10);
		btnItems[11] = (Button) findViewById(R.id.btn11);
		btnItems[12] = (Button) findViewById(R.id.btn12);
		btnItems[13] = (Button) findViewById(R.id.btn13);// P2 Kalesi

		switch (Integer.parseInt(diff)) {
		case 1:
			// easy oyun ayarland�
			h[4] = true;// h5 ve h6 heuristic ve 1 derinlikte �al��acak yapay
						// zeka ayarland�.
			h[5] = true;
			player1.reset(h, 1);

			for (int i1 = 0; i1 < 6; i1++)
				h[i1] = false;
			h[4] = true;// h5 ve h6 heuristic ve 1 derinlikte �al��acak yapay
						// zeka ayarland�.
			h[5] = true;
			player1.reset(h, 1);
			Toast.makeText(PlayGameActivity.this, "easy", Toast.LENGTH_SHORT)
					.show();
			break;
		case 2:
			// medium oyun ayarland�
			h[0] = true;// h1 heuristic ve 3 derinlikte �al��acak yapay zeka
						// ayarland�.
			player1.reset(h, 3);

			for (int i2 = 0; i2 < 6; i2++)
				h[i2] = false;
			h[0] = true;// h1 heuristic ve 3 derinlikte �al��acak yapay zeka
						// ayarland�.
			player1.reset(h, 3);
			Toast.makeText(PlayGameActivity.this, "medium", Toast.LENGTH_SHORT)
					.show();
			break;
		case 3:
			// hard oyun ayarland�
			h[0] = true;// h1 ve h3 heuristic ve 8 derinlikte �al��acak yapay
						// zeka ayarland�.
			h[2] = true;
			player1.reset(h, 8);

			for (int i3 = 0; i3 < 6; i3++)
				h[i3] = false;
			h[0] = true;// h1 ve h3 heuristic ve 8 derinlikte �al��acak yapay
						// zeka ayarland�.
			h[2] = true;
			player1.reset(h, 8);
			Toast.makeText(PlayGameActivity.this, "hard", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}

		for (int k = 0; k < item.length; k++) {
			item[k].setEnabled(true);
			if (k == 6 || k == 13) {
				item[k].setText("0");
			} else {
				item[k].setText(Integer.toString(ai.CBoard.StartingStones));
			}
		}

		btnItems[0].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[0].getText(),
						Toast.LENGTH_SHORT).show();

				binIndex = 0;
				numberOfStone = Integer.parseInt(item[0].getText().toString());
//				setRightToLeftAnimation(numberOfStone, binIndex);
				// if (numberOfStone <= binIndex) {
				// // select Integer.parseInt(item[0].getText().toString())
				// setRightToLeftAnimation(numberOfStone, binIndex);
				// } else {
				// // select 5
				// setRightToLeftAnimation(binIndex, binIndex);
				// }
				if (!gameMode) {
					OynaHH(1);
					// item[7].setText("bast�m");
				}
			}
		});
		btnItems[1].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[1].getText(),
						Toast.LENGTH_SHORT).show();

				binIndex = 1;
				numberOfStone = Integer.parseInt(item[1].getText().toString());

				// if (numberOfStone <= binIndex) {
				// // select Integer.parseInt(item[0].getText().toString())
				// setRightToLeftAnimation(numberOfStone, binIndex);
				// } else {
				// // select 5
				// setRightToLeftAnimation(binIndex, binIndex);
				// }

				if (!gameMode) {
					OynaHH(2);
				}
			}
		});
		btnItems[2].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[2].getText(),
						Toast.LENGTH_SHORT).show();

				binIndex = 2;
				numberOfStone = Integer.parseInt(item[2].getText().toString());
//				if (numberOfStone <= binIndex) {
//					// select Integer.parseInt(item[0].getText().toString())
//					setRightToLeftAnimation(numberOfStone, binIndex);
//				} else {
//					// select 5
//					setRightToLeftAnimation(binIndex, binIndex);
//				}

				if (!gameMode) {
					OynaHH(3);
				}
			}
		});
		btnItems[3].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[3].getText(),
						Toast.LENGTH_SHORT).show();

				binIndex = 3;
				numberOfStone = Integer.parseInt(item[3].getText().toString());
//				if (numberOfStone <= binIndex) {
//					// select Integer.parseInt(item[0].getText().toString())
//					setRightToLeftAnimation(numberOfStone, binIndex);
//				} else {
//					// select 5
//					setRightToLeftAnimation(binIndex, binIndex);
//				}

				if (!gameMode) {
					OynaHH(4);
				}
			}
		});
		btnItems[4].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[4].getText(),
						Toast.LENGTH_SHORT).show();

				binIndex = 4;
				numberOfStone = Integer.parseInt(item[4].getText().toString());
//				if (numberOfStone <= binIndex) {
//					// select Integer.parseInt(item[0].getText().toString())
//					setRightToLeftAnimation(numberOfStone, binIndex);
//				} else {
//					// select 5
//					setRightToLeftAnimation(binIndex, binIndex);
//				}

				if (!gameMode) {
					OynaHH(5);
				}
			}
		});
		btnItems[5].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[5].getText(),
						Toast.LENGTH_SHORT).show();

				binIndex = 5;
				numberOfStone = Integer.parseInt(item[5].getText().toString());
//				if (numberOfStone <= binIndex) {
//					// select Integer.parseInt(item[0].getText().toString())
//					setRightToLeftAnimation(numberOfStone, binIndex);
//				} else {
//					// select 5
//					setRightToLeftAnimation(binIndex, binIndex);
//				}

				if (!gameMode) {
					OynaHH(6);
				}
			}
		});
		btnItems[7].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[7].getText(),
						Toast.LENGTH_SHORT).show();
				if (gameMode) {
					OynaCH(1);
				} else {
					OynaHH(1);
				}
			}
		});
		btnItems[8].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[8].getText(),
						Toast.LENGTH_SHORT).show();
				if (gameMode) {
					OynaCH(2);
				} else {
					OynaHH(2);
				}
			}
		});
		btnItems[9].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[9].getText(),
						Toast.LENGTH_SHORT).show();
				if (gameMode) {
					OynaCH(3);
				} else {
					OynaHH(3);
				}
			}
		});
		btnItems[10].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[10].getText(),
						Toast.LENGTH_SHORT).show();
				if (gameMode) {
					OynaCH(4);
				} else {
					OynaHH(4);
				}
			}
		});
		btnItems[11].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[11].getText(),
						Toast.LENGTH_SHORT).show();
				if (gameMode) {
					OynaCH(5);
				} else {
					OynaHH(5);
				}
			}
		});

		btnItems[12].setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PlayGameActivity.this, item[12].getText(),
						Toast.LENGTH_SHORT).show();
				if (gameMode) {
					OynaCH(6);
				} else {
					OynaHH(6);
				}
			}
		});

		final Button btnGoHome = (Button) findViewById(R.id.btnGoBackHome);
		btnGoHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PlayGameActivity.this.finish();
			}
		});
	}/* end onCreate */

	/* my functions */
	// -----------------------------------------------------------------------------------------
	// Method: Oyna
	// De�i�kenleri: int bin (player2 nin hangi �ukuru oynamak istedi�ini
	// tutar.)
	// D�n�� De�eri: None
	// Ama�: player2 ye oynama hakk� verir, o oynad�ktan sonra player1 oynar.
	int player1Move;

	public void OynaHH(int bin) {
		if (MainBoard.Status == 1) // s�ra bizdeyse oyna, bizden sonra rakibe
		// ge�erse o oynar.
		{

			MainBoard.MakeCopyIn(copyBoard);
			MainBoard.MakeMove(bin);
			PlayOnBoard(bin);

			if (MainBoard.Status > 2)
				GameResults();
		} else
		// if (MainBoard.Status == 2) // s�ra bizdeyse oyna, bizden sonra rakibe
		// // ge�erse o oynar.
		{

			MainBoard.MakeCopyIn(copyBoard);
			MainBoard.MakeMove(bin);
			PlayOnBoard(bin);
			if (MainBoard.Status > 2)
				GameResults();
		}
	}

	public void OynaCH(int bin) {
		player1Move = 0;

		if (MainBoard.Status == 2) // s�ra bizdeyse oyna, bizden sonra rakibe
									// ge�erse o oynar.
		{
			MainBoard.MakeCopyIn(copyBoard);
			MainBoard.MakeMove(bin);
			PlayOnBoard(bin);
			if (MainBoard.Status > 2)
				GameResults();
		}

		// if (MainBoard.Status == 1) {
		// pd = ProgressDialog.show(this, "Working..", "Calculating Pi", true,
		// false);
		//
		// Thread thread = new Thread(this);
		// thread.start();
		// }

		while (MainBoard.Status == 1)// s�ra rakipteyse status de�i�ene(s�ra
		// ge�ene) kadar yapay zeka oynar.
		{//

			player1Move = player1.getMove(MainBoard);
			MainBoard.MakeCopyIn(copyBoard);
			MainBoard.MakeMove(player1Move);
			PlayOnBoard(player1Move);
			if (MainBoard.Status > 2)
				GameResults();
		}

	}/* end Oyna() */

	public void GameResults() {
		// if (MainBoard.Status == 3)
		// MessageBox.Show("Computer wins the game");
		// if (MainBoard.Status == 4)
		// MessageBox.Show("You win the game");
		// if (MainBoard.Status == 5)
		// MessageBox.Show("The game was a tie");
	}

	public void PlayOnBoard(int move) {
		// de�erleri atama
		int numStonesInBin;
		int tempCounter = 0;

		int arrayIndexOfP1Move = move - 1;
		int indexOfP1FirstDropStone = arrayIndexOfP1Move + 1;

		int arrayIndexOfP2Move = (move - 1) + (CBoard.holes + 1);
		int indexOfP2FirstDropStone = arrayIndexOfP2Move + 1;

		int moduloIndex = (CBoard.holes + 1) * 2;

		int indexOfP1FirstBin = 0;
		int indexOfP1LastBin = CBoard.indexP1Home - 1;

		int indexOfP2FirstBin = CBoard.holes + 1;
		int indexOfP2LastBin = CBoard.indexP2Home - 1;

		int x;// counter
		boolean GoAgain = false;
		int sum1 = 0;
		int sum2 = 0;
		boolean GameOver = false;

		// hamle uygun ise oyuncu bu hamleyi yapabilir
		if (copyBoard.ValidMove(move)) {
			switch (copyBoard.Status)// oyuncunun hamlesi ise, manipule et
			{
			case 1: {
				// P1 taraf�ndaki ta�lar�n say�s�
				numStonesInBin = copyBoard.gameBoard[arrayIndexOfP1Move];

				// Bo� oyuklar kald�r�l�r
				copyBoard.gameBoard[arrayIndexOfP1Move] = 0;
				item[arrayIndexOfP1Move].setText("0");
				// Ta�lar� saatin tersi y�n�nde da��t
				// Rakibin kalesini atla
				for (x = 0; x < (numStonesInBin + tempCounter); x++)// burada
																	// ta�lar
																	// da��t�lacak
				{

					// kar��n�n kalesi de�ilse ta� koy
					if (!(((indexOfP1FirstDropStone + x) % moduloIndex) == CBoard.indexP2Home)) {
						copyBoard.gameBoard[(indexOfP1FirstDropStone + x)
								% moduloIndex]++;

						/* ?? */item[(indexOfP1FirstDropStone + x)
								% moduloIndex]
								.setText(Integer
										.toString(copyBoard.gameBoard[(indexOfP1FirstDropStone + x)
												% moduloIndex]));
						// oynanan �ukurdaki ta� say�s� kadar buraya giriyor
						// Toast.makeText(PlayGameActivity.this,
						// Integer.toString(x), Toast.LENGTH_SHORT).show();
						// setleftToRightAnimation();

						setRightToLeftAnimation(x+1, binIndex++);
						
					} else
						tempCounter++;
				}
				
				// setRightToLeftAnimation(x);
				// capture u kontrol et
				int indexOfBinOfLastStone = (indexOfP1FirstDropStone + x - 1)
						% (moduloIndex);

				// bizim tarafta isek
				if ((indexOfBinOfLastStone >= indexOfP1FirstBin)
						&& (indexOfBinOfLastStone <= indexOfP1LastBin)) {
					// goal ise
					int currBin = indexOfBinOfLastStone + 1;// currBin 1-6 aras�
															// bin no su.

					// son ta��n at�ld��� yer
					int oppositeBin = CBoard.holes + 1 - currBin;// currBin in
																	// kar��s�ndaki
																	// bin no
																	// su.

					int indexOfOppositeBin = oppositeBin + CBoard.holes;

					if ((copyBoard.gameBoard[indexOfBinOfLastStone] == 1) && // son
																				// ta��n
																				// koyuldugu
																				// binde
																				// 1
																				// tas
																				// olmal�
							(copyBoard.gameBoard[indexOfOppositeBin] > 0) && // son
																				// ta��n
																				// koyuldu�u
																				// oyugun
																				// kars�s�
																				// bo�
																				// olmamal�
							(indexOfBinOfLastStone != CBoard.indexP1Home))// son
																			// ta��n
																			// koyuldugu
																			// home
																			// olmamal�
					{

						copyBoard.gameBoard[CBoard.indexP1Home] += copyBoard.gameBoard[indexOfBinOfLastStone]
								+ copyBoard.gameBoard[indexOfOppositeBin];
						item[CBoard.indexP1Home]
								.setText(Integer
										.toString(copyBoard.gameBoard[CBoard.indexP1Home]));

						copyBoard.gameBoard[indexOfBinOfLastStone] = 0;
						copyBoard.gameBoard[indexOfOppositeBin] = 0;
						item[indexOfBinOfLastStone].setText("0");
						item[indexOfOppositeBin].setText("0");
					}
				}

				if (indexOfBinOfLastStone == CBoard.indexP1Home)// Ta�
																// da��t�ld�ktan
																// sonra status
																// ayar� i�in
					GoAgain = true;
				break;
			}

			// oyuncunun hamlesi ise, manipule et
			case 2: {
				// p2 taraf�ndaki ta�lar�n say�s�
				numStonesInBin = copyBoard.gameBoard[arrayIndexOfP2Move];

				// Bo� oyuklar kald�r�l�r
				copyBoard.gameBoard[arrayIndexOfP2Move] = 0;// burada label i
															// de�i�tir
				item[arrayIndexOfP2Move].setText("0");
				// Ta�lar� saatin tersi y�n�nde da��t
				// Rakibin kalesini atla
				for (x = 0; x < (numStonesInBin + tempCounter); x++) {
					// kale de�ilse ta� koy
					if (!(((indexOfP2FirstDropStone + x) % moduloIndex) == CBoard.indexP1Home)) {
						copyBoard.gameBoard[(indexOfP2FirstDropStone + x)
								% moduloIndex]++;
						item[(indexOfP2FirstDropStone + x) % moduloIndex]
								.setText(Integer
										.toString(copyBoard.gameBoard[(indexOfP2FirstDropStone + x)
												% moduloIndex]));

						// animasyon i�in
						// Toast.makeText(PlayGameActivity.this,
						// Integer.toString(x), Toast.LENGTH_SHORT).show();

					} else
						tempCounter++;
				}
				// setRightToLeftAnimation(x);
				// capture u kontrol et
				int indexOfBinOfLastStone = (indexOfP2FirstDropStone + x - 1)
						% moduloIndex;

				// kar�� tarafta isek
				if ((indexOfBinOfLastStone >= indexOfP2FirstBin)
						&& (indexOfBinOfLastStone <= indexOfP2LastBin)) {
					// son ta��n b�rak�ld��� oyuk
					int currBin = indexOfBinOfLastStone - CBoard.holes;

					int oppositeBin = CBoard.holes + 1 - currBin;

					int indexOfOppositeBin = oppositeBin - 1;

					if ((copyBoard.gameBoard[indexOfBinOfLastStone] == 1)
							&& (copyBoard.gameBoard[indexOfOppositeBin] > 0)
							&& (indexOfBinOfLastStone != CBoard.indexP2Home)) {
						copyBoard.gameBoard[CBoard.indexP2Home] += copyBoard.gameBoard[indexOfBinOfLastStone]
								+ copyBoard.gameBoard[indexOfOppositeBin];
						item[CBoard.indexP2Home]
								.setText(Integer
										.toString(copyBoard.gameBoard[CBoard.indexP2Home]));
						copyBoard.gameBoard[indexOfBinOfLastStone] = 0;
						copyBoard.gameBoard[indexOfOppositeBin] = 0;
						item[indexOfBinOfLastStone].setText("0");
						item[indexOfOppositeBin].setText("0");
					}
				}

				if (indexOfBinOfLastStone == CBoard.indexP2Home)
					GoAgain = true;
				break;
			}
			default:
				break;
			}

			// kimin oynayaca��n� belirle
			if (!GoAgain) {
				// P1 oynad� P2ye ge�
				if (copyBoard.Status == 1) {
					copyBoard.Status = 2;
					for (int i = 7; i < 13; i++) {
						btnItems[i].setEnabled(true);
					}
					for (int i = 0; i < 6; i++) {
						btnItems[i].setEnabled(false);
					}
				}// P2 oynad� P1e ge�
				else if (copyBoard.Status == 2) {
					copyBoard.Status = 1;
					for (int i = 0; i < 6; i++) {
						btnItems[i].setEnabled(true);
					}
					for (int i = 7; i < 13; i++) {
						btnItems[i].setEnabled(false);
					}
				}
			}

			sum1 = sum2 = 0;

			for (x = 0; x < CBoard.holes; x++) {
				sum1 += copyBoard.gameBoard[x];
				sum2 += copyBoard.gameBoard[x + (CBoard.holes + 1)];
			}

			// P1 taraf�nda ta� kalmad�ysa kalan ta�lar� aktar oyunu bitir
			if (sum1 == 0) {
				// P2 taraf�nda kalan ta�lar� aktar
				copyBoard.gameBoard[CBoard.indexP2Home] += sum2;
				item[CBoard.indexP2Home].setText(Integer
						.toString(copyBoard.gameBoard[CBoard.indexP2Home]));

				// oyukar� s�f�rla
				for (x = 0; x < CBoard.holes; x++) {
					copyBoard.gameBoard[x + (CBoard.holes + 1)] = 0;
					item[x + (CBoard.holes + 1)].setText("0");
				}

				// oyun bitti�ini belirt
				GameOver = true;
			}

			// P2 taraf�nda ta� kalmad�ysa kalan ta�lar� aktar oyunu bitir
			else if (sum2 == 0) {
				// P1 taraf�nda kalan ta�lar� aktar
				copyBoard.gameBoard[CBoard.indexP1Home] += sum1;
				item[CBoard.indexP1Home].setText(Integer
						.toString(copyBoard.gameBoard[CBoard.indexP1Home]));// /////////////////////

				// oyukar� s�f�rla
				for (x = 0; x < CBoard.holes; x++) {
					copyBoard.gameBoard[x] = 0;
					item[x].setText("0");
				}

				// oyun bitti�ini belirt
				GameOver = true;
			}

			// Kim kazand�?
			if (GameOver) {
				// P1
				if (copyBoard.gameBoard[CBoard.indexP1Home] > copyBoard.gameBoard[CBoard.indexP2Home])
					copyBoard.Status = 3;
				// P2
				else if (copyBoard.gameBoard[CBoard.indexP2Home] > copyBoard.gameBoard[CBoard.indexP1Home])
					copyBoard.Status = 4;
				// berabere
				else
					copyBoard.Status = 5;
			}
		} else // e�er do�ru bir hamle yapm�yorsa uyar� ver.
		{
			// MessageBox.Show("This is not a valid move!");
		}
	}/* end PlayOnBoard */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)// donan�msal olarak geri butonuna
												// bas�l�rsa
		{
			// are you sure to end game?
			// finish();
			return true;
		}
		return false;
	}
}
