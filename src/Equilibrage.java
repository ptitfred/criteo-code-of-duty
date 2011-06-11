
import java.io.*;
import java.util.*;

public class Equilibrage {

	static class Dataset {
		int[] numbers;
		Dataset(int size) {
			numbers = new int[size];
			hasSolution = false;
			steps = new LinkedList<int[]>();
		}

		boolean hasSolution;
		Collection<int[]> steps;
	}

	Collection<Dataset> datasets;

	public void go() throws Exception {
		readInput();
		for (Dataset : datasets) {
			// TODO multi threader ca
			resolve(d);
		}
		writeOutput();
	}

	private void resolve(Dataset d) {
		
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
		for (Dataset dataset : this.datasets) {
			bw.println("-1");
			bw.println();
		}
		bw.flush();
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

