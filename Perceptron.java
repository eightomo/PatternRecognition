import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Perceptron {

	public static final int Class1_Incorrect = 1;// クラス1のデータをクラス2に誤って分類した際のフラグ
	public static final int Class2_Incorrect = 2;// クラス2のデータをクラス1に誤って分類した際のフラグ
	public static final int Correct = 0;// 正しく分類された際のフラグ
	static double Low = 1.2;// ρ
	static boolean finish_flag;// 全てのデータセットに対して正しく分類できた時:1 それ以外:0

	/*
	 * 分類チェック関数
	 *
	 * @param gx 識別関数の値
	 * @param class_num 正しいクラス番号
	 *
	 * @return 分類フラグ 0:正しい 1:ω1->ω2 2:ω2->ω1
	 */
	public static int classify_check(double gx, double class_num) {
		if (gx < 0 && class_num == 1) {
			return Class1_Incorrect;
		} else if (gx > 0 && class_num == 2) {
			return Class2_Incorrect;
		} else {
			return Correct;
		}
	}

	/*
	 * 重みの更新関数
	 *
	 * @param update_id 分類フラグ
	 * @param input_data 入力データx
	 * @param w 重み
	 *
	 */
	public static void update_weight(int update_id, double input_data, double[]w) {
		if (update_id == Class1_Incorrect) {
			w[0] += Low * 1;
			w[1] += Low * input_data;
			System.out.println(w[0] + " " + w[1]);
		} else if (update_id == Class2_Incorrect) {
			w[0] -= Low * 1;
			w[1] -= Low * input_data;
			System.out.println(w[0] + " " + w[1]);
		}
	}

	/*
	 * 初期値出力関数
	 *
	 * @param w 重み
	 * @param low ρ
	 *
	 */
	public static void print_initial(double[]w, double low) {
		System.out.println("w0 = " + w[0] + ", w1 = " + w[1] + ", ρ = " + low);
		System.out.println(w[0] + " " + w[1]);

	}

	/*
	 * 配列のシャッフル関数（今回使用していない）
	 *
	 * @param データセット配列
	 *
	 */
	static void shuffleArray(int[] ar) {
		// If running on Java 6 or older, use `new Random()` on RHS here
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	/*
	 * ファイルが読み込めるチェックする関数
	 *
	 * @param file データセットファイル
	 *
	 * @return true
	 *
	 */
	public static boolean checkBeforeReadfile(File file) {
		if (file.exists()) {
			if (file.isFile() && file.canRead()) {
				return true;
			}
		}

		return false;
	}

	/*
	 * ファイル読み込み関数
	 *
	 * @param file 読み込むファイル
	 * @param x データセットを格納するための配列
	 *
	 */
	public static void ReadFile(String file_name, double[][] x) {
		// データセットファイルの読み込み
		try {
			File file = new File(file_name);

			if (checkBeforeReadfile(file)) {
				BufferedReader br = new BufferedReader(new FileReader(file));

				String line;
				int number = 0;
				while ((line = br.readLine()) != null) {

					String str[] = line.split(",");

					for (int j = 0; j < 2; j++) {
						x[number][j] = Double.valueOf(str[j]);

					}
					number++;
				}

				br.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/*
	 * main関数
	 */
	public static void main(String[] args) {
		// 重みの初期化 w0, w1
		double w[] = {-7.0, 2.0};

		// 初期値を出力
		print_initial(w, Low);

		// データセット配列の初期化
		double x[][] = new double[6][2];

		// ファイル読み込み→x配列に格納
		ReadFile("dataset.txt", x);

		while (finish_flag == false)// もし全て正しく分類出来たら flagが1となり終了
		{
			int checkforupdates = 0;// 更新が行われた際，分類フラグが格納される
			for (int i = 0; i < x.length; i++) {
				int update_id = classify_check(w[0] + w[1] * x[i][0], x[i][1]);
				checkforupdates += update_id;
				update_weight(update_id, x[i][0], w);
			}
			if (checkforupdates == 0)// 更新がなかったらfinish_flagを立て，抜ける
			{
				finish_flag = true;
			}
		}
	}
}
