package ai;

public class CBoard {
    public static int holes = 6;    /* ki�i ba�� 6�ar oyuk*/       
    public static int StartingStones;   //oyuk ba�� 4er ta�
    public static int TotalStones; // toplam ta� say�s�	
    
    /* THE BOARD:
    							P1[6]  P1[5]  P1[4]  P1[3]  P1[2]  P1[1]
    	                Mancala1                                        Mancala2
    							P2[1]  P2[2]  P2[3]  P2[4]  P2[5]  P2[6]
    indisleme P1[1] den ba�lay�p, saat y�n�n�n tersi y�nde Mancala2 ye do�ru gidiyor.*/   
    
    public int[] gameBoard = new int[(holes + 1) * 2]; //14 elemanl� gameBoard dizisi       
    public static int indexP1Home = holes; //indexP1Home=6
    public static int indexP2Home = ((holes + 1) * 2) - 1;//indexP2Home=13   
    /* Statuse g�re hamleler
    	(1) s�ra P1de
    	(2) s�ra P2de
    	(3) P1 kazand�
    	(4) P2 kazand�
    	(5) Berabere*/
    public int Status;
    
    /*yeni oyun ba�lat*/
    public CBoard() 
    {
        NewGame();
    }
    
    // -----------------------------------------------------------------------------------------
    // Method: NewGame()
    // De�i�kenleri: -
    // D�n�� De�eri: -
    // Ama�: de�erler atan�r 
    //	1. oyuncu i�in
    // -----------------------------------------------------------------------------------------
    public void NewGame()
    {
        // belirtilen say�lar� oyuklara ata
        for (int x = 0; x < holes; x++)
        {
            gameBoard[x] = StartingStones;//P1 oyuklar�n� doldur
            gameBoard[x + (holes + 1)] = StartingStones;//P2 oyuklar�n� doldur
        }

        indexP1Home = holes;
        indexP2Home = ((holes + 1) * 2) - 1;

        // kaleleri s�f�rla
        gameBoard[indexP1Home] = 0;//gameBoard[6]=0
        gameBoard[indexP2Home] = 0;//gameBoard[13]=0

        // s�ray� P1e ver
        Status = 2;
    }
    
    // -----------------------------------------------------------------------------------------
    // Method: ValidMove
    // De�i�kenleri: her iki player i�inde bin index i 1-6 aras�nda.
    // D�n�� De�eri: bool: uygun olup olmad���n� d�nd�r
    // Ama�: uygun mu de�il mi tespit et 
    // -----------------------------------------------------------------------------------------
    public boolean ValidMove(int move)
    {
    	boolean testValid = false;

        if ((move >= 1) && (move <= holes))/*1 <= move <= 6*/
        {
            if ((Status == 1) && (gameBoard[move - 1] > 0))
                testValid = true;/*S�ra P1 de ve ???*/
            if ((Status == 2) && (gameBoard[(move - 1) + (holes + 1)] > 0))
                testValid = true;/*S�ra P2 de ve ???*/
        }
        return testValid;
    }
    
 // -----------------------------------------------------------------------------------------
    // Method: MakeMove
    // De�i�kenleri: oynanmak istenen hamle
    // D�n�� De�eri: -
    // Ama�: Ta�lar� saatin tersi y�n�nde da��t
     // -----------------------------------------------------------------------------------------
    public void MakeMove(int move)
    {
        int numStonesInBin;
        int tempCounter = 0;

        int arrayIndexOfP1Move = move - 1;
        int indexOfP1FirstDropStone = arrayIndexOfP1Move + 1;

        int arrayIndexOfP2Move = (move - 1) + (holes + 1);
        int indexOfP2FirstDropStone = arrayIndexOfP2Move + 1;

        int moduloIndex = (holes + 1) * 2;

        int indexOfP1FirstBin = 0;
        int indexOfP1LastBin = indexP1Home - 1;

        int indexOfP2FirstBin = holes + 1;
        int indexOfP2LastBin = indexP2Home - 1;

        int x;//counter
        boolean GoAgain = false;
        int sum1, sum2;
        boolean GameOver = false;

        // hamle uygun ise oyuncu bu hamleyi yapabilir
        if (ValidMove(move))
        {
            switch (Status)
            {
                case 1:
                    {
                        // Oyuncu taraf�ndaki ta�lar
                        numStonesInBin = gameBoard[arrayIndexOfP1Move];

                        // Bo� oyuklar kald�r�l�r
                        gameBoard[arrayIndexOfP1Move] = 0;

                        // Ta�lar� saatin tersi y�n�nde da��t
                        // Rakibin kalesini atla
                        for (x = 0; x < (numStonesInBin + tempCounter); x++)
                        {
                            // kar��n�n kalesi de�ilse ta� koy
                            if (!(((indexOfP1FirstDropStone + x) % moduloIndex) == indexP2Home))
                            {
                                gameBoard[(indexOfP1FirstDropStone + x) % moduloIndex]++;
                            }
                            else 
                                tempCounter++;
                        }

                        // capture u kontrol et
                        int indexOfBinOfLastStone = (indexOfP1FirstDropStone + x - 1) % (moduloIndex);

                        // bizim tarafta isek
                        if ((indexOfBinOfLastStone >= indexOfP1FirstBin) &&
                                (indexOfBinOfLastStone <= indexOfP1LastBin))
                        {
                            // goal ise
                            int currBin = indexOfBinOfLastStone + 1;//currBin 1-6 aras� bin no su.	

                            // son ta��n at�ld��� yer
                            int oppositeBin = holes + 1 - currBin;//currBin in kar��s�ndaki bin no su.

                            int indexOfOppositeBin = oppositeBin + holes;

                            if ((gameBoard[indexOfBinOfLastStone] == 1) &&//son ta��n koyuldu�u bin de 1 ta� olmal�
                                    (gameBoard[indexOfOppositeBin] > 0) &&//son ta��n koyuldu�u binin kar��s� bo� olmamal�
                                    (indexOfBinOfLastStone != indexP1Home))//son ta��n koyuldu�u home olmamal�.
                            {
                                gameBoard[indexP1Home] += gameBoard[indexOfBinOfLastStone] +
                                                          gameBoard[indexOfOppositeBin];
                                gameBoard[indexOfBinOfLastStone] = 0;
                                gameBoard[indexOfOppositeBin] = 0;
                            }
                        }

                        if (indexOfBinOfLastStone == indexP1Home)
                            GoAgain = true;
                        break;
                    }

                // P2 oyuncunun hamlesi ise, manipule et
                case 2:
                    {
                        // p2 taraf�ndaki ta�lar�n say�s�
                        numStonesInBin = gameBoard[arrayIndexOfP2Move];

                        // Bo� oyuklar kald�r�l�r
                        gameBoard[arrayIndexOfP2Move] = 0;

                        // Ta�lar� saatin tersi y�n�nde da��t
                        // Rakibin kalesini atla
                        for (x = 0; x < (numStonesInBin + tempCounter); x++)
                        {
                            // kale de�ilse ta� koy
                            if (!(((indexOfP2FirstDropStone + x) % moduloIndex) == indexP1Home))
                            {
                                gameBoard[(indexOfP2FirstDropStone + x) % moduloIndex]++;
                            }
                            else
                                tempCounter++; 
                        }

                        //  capture u kontrol et
                        int indexOfBinOfLastStone = (indexOfP2FirstDropStone + x - 1) % moduloIndex;

                        // kar�� tarafta isek
                        if ((indexOfBinOfLastStone >= indexOfP2FirstBin) && (indexOfBinOfLastStone <= indexOfP2LastBin))
                        {
                            // son ta��n b�rak�ld��� oyuk
                            int currBin = indexOfBinOfLastStone - holes;

                            int oppositeBin = holes + 1 - currBin;

                            int indexOfOppositeBin = oppositeBin - 1;

                            if ((gameBoard[indexOfBinOfLastStone] == 1) && (gameBoard[indexOfOppositeBin] > 0) && (indexOfBinOfLastStone != indexP2Home))
                            {
                                gameBoard[indexP2Home] += gameBoard[indexOfBinOfLastStone] + gameBoard[indexOfOppositeBin];
                                gameBoard[indexOfBinOfLastStone] = 0;
                                gameBoard[indexOfOppositeBin] = 0;
                            }
                        }

                        if (indexOfBinOfLastStone == indexP2Home)
                            GoAgain = true;
                        break;
                    }
                default: break;
            }

            // kimin oynayaca��n� belirle
            if (!GoAgain)
            {
                // P1 oynad� P2ye ge�
                if (Status == 1)
                    Status = 2;
                // P2 oynad� P1e ge�
                else if (Status == 2)
                    Status = 1;
            }

            sum1 = sum2 = 0;

            for (x = 0; x < holes; x++)
            {
                sum1 += gameBoard[x];
                sum2 += gameBoard[x + (holes + 1)];
            }

            //P1 taraf�nda ta� kalmad�ysa kalan ta�lar� aktar oyunu bitir
            if (sum1 == 0)
            {
                // P2 taraf�nda kalan ta�lar� aktar
                gameBoard[indexP2Home] += sum2;

                // oyukar� s�f�rla
                for (x = 0; x < holes; x++)
                     gameBoard[x + (holes + 1)] = 0;

                // oyun bitti�ini belirt
                GameOver = true;
            }

            // P2 taraf�nda ta� kalmad�ysa kalan ta�lar� aktar oyunu bitir
            else if (sum2 == 0)
            {
                // P1 taraf�nda kalan ta�lar� aktar
                gameBoard[indexP1Home] += sum1;

                // oyukar� s�f�rla
                for (x = 0; x < holes; x++)
                    gameBoard[x] = 0;

                // oyun bitti�ini belirt
                GameOver = true;
            }

            // Kim kazand�?
            if (GameOver)
            {
                // P1's
                if (gameBoard[indexP1Home] > gameBoard[indexP2Home])
                    Status = 3;
                // P2's 
                else if (gameBoard[indexP2Home] > gameBoard[indexP1Home])
                    Status = 4;
                // Berabere
                else
                    Status = 5;
            }
        }
    }
    
    public void OutputBoard(int bin){}			// ekrana yazd�rma
    
    // -----------------------------------------------------------------------------------------
    // Method: MakeCopyIn
    // De�i�kenleri: CBoard *destination (pointer to the copy of the current board's configuration)
    // D�n�� De�eri: -
    // Ama�: kullan�lan tahtan�n ayarlar�n� kopyalar
    // -----------------------------------------------------------------------------------------
    public void MakeCopyIn(CBoard destination)
    {
        //destination = new CBoard();
        // dizideki de�erleri kopyala
        for (int x = 0; x < ((holes + 1) * 2); x++)
            destination.gameBoard[x] = this.gameBoard[x];
   
        // status de�erini kopyala
        destination.Status = Status;
        CBoard.indexP1Home = holes;
        CBoard.indexP2Home = ((holes + 1) * 2) - 1;
    }
    
    // -------------------------------------------------------------------
    // struct: Node
    // Ama�: a�a� yap�s�ndaki bilgileri tutmak i�in
    // -------------------------------------------------------------------
//    class Node
//    {
//        public CBoard board;
//        public int evalFunc;
//        public int minimaxMove;
//        public int nodeDepth;
//    }; 
}
