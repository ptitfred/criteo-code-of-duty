
import java.io.*;
import java.util.*;

public class Equilibrage {

	static class Dataset {
		int size;
		int[] numbers;

		boolean hasSolution;
		Collection<int[]> steps;

		Dataset(int size) {
			this.size = size;
			this.numbers = new int[size];
			this.hasSolution = false;
			this.steps = new LinkedList<int[]>();
		}

	}

	Collection<Dataset> datasets;

	public void go() throws Exception {
		readInput();
		for (Dataset d : datasets) {
			// TODO multi threader ca
			balance(d);
		}
		writeOutput();
	}

	private void balance(Dataset d) {
		int total =0;
		for (int ni : d.numbers) {
			total += ni;
		}
		if (total % d.size == 0) {
			d.hasSolution = true;
			int target = total / d.size;

			int max = 0;
			int[] previousStep = d.numbers;
			do {
				max = 0;

				int[] diff = new int[d.size];
				int imax = -1;
				for (int i=0; i<d.size; i++) {
					diff[i] = previousStep[i+1 == d.size ? 0 : i+1] - previousStep[i];
					if (Math.abs(diff[i]) > max) { max = Math.abs(diff[i]); imax = i; }
				}
				int swap_pointer = imax;

				if (max > 0) {
					int[] step = new int[d.size];
					System.arraycopy(previousStep, 0, step, 0, d.size);
					while (swap_pointer < imax + d.size -1) {
						int from = (swap_pointer - 1) % d.size;
						if (from <0) from += d.size;
						int to = swap_pointer % d.size;
						// ex:
						//   si swap_pointer = 1, numbers[0] <> numbers[1]
						//   si diff[swap_pointer] > 0, numbers[0]++ et numbers[1]--
						//   sinon numbers[0]-- et numbers[1]++
					
						int quantum = diff[to] < 0 ? 1 : -1;
						if (step[from] != target && step[to] != target) {
							step[from] += quantum;
							step[to] -= quantum;
						}

						swap_pointer += 2;
					}
					d.steps.add(step);
					print(System.out, -1, step);
					previousStep = step;
				}
			} while (max > 0 && d.steps.size() < 5);
		}
	}

	private void readInput() throws Exception {
		this.datasets = new ArrayList<Dataset>();
		// charger l'input
		FileInputStream fis = new FileInputStream("input.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String buffer = null;
		int row = 0;
		while ((buffer = br.readLine()) != null) {
			row ++;
			int count = Integer.parseInt(buffer);
			if (count > 0) {
				Dataset d = new Dataset(count);
				this.datasets.add(d);
				buffer = br.readLine(); row ++;
				String[] datas = buffer.split(" ");
				if (datas.length != count) {
					throw new Exception("input.txt ne respecte pas le format ; ligne " + row);
				}
				for (int i=0; i<count; i++) {
					d.numbers[i] = Integer.parseInt(datas[i]);
				}
				br.readLine(); row ++;
			}
		}
	}

	private void writeOutput() throws IOException {
		File f = new File("output.txt");
		if (!f.exists()) f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f);
		PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));
		boolean first = true;
		for (Dataset dataset : this.datasets) {
			if (first) first = false; else bw.println();
			if (dataset.hasSolution) {
				bw.println(dataset.steps.size());
				print(bw, 0, dataset.numbers);
				int stepNumber = 1;
				for (int[] step : dataset.steps) {
					print(bw, stepNumber, step);
					stepNumber++;
				}
			} else {
				bw.println("-1");
			}
		}
		bw.flush();
	}

	void print(PrintWriter w, int stepNumber, int[] nis) {
		w.print(stepNumber);
		w.print(" : ");
		w.print("(");
		boolean first = true;
		for (int ni : nis) {
			if (first) first = false; else w.print(", ");
			w.print(ni);
		}
		w.print(")");
		w.println();
	}

	void print(PrintStream w, int stepNumber, int[] nis) {
		w.print(stepNumber);
		w.print(" : ");
		w.print("(");
		boolean first = true;
		for (int ni : nis) {
			if (first) first = false; else w.print(", ");
			w.print(ni);
		}
		w.print(")");
		w.println();
	}

	public static void main(String[] args) {
		try {
			new Equilibrage().go();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}

