
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
			int[] diff = new int[d.size];
			for (int i=0; i<d.size; i++) {
				diff[i] = d.numbers[i] - target;
			}
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
				int stepNumber = 0;
				print(bw, stepNumber, dataset.numbers);
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

