
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
		int max=0;
		for (int ni : d.numbers) {
			total += ni;
			if (ni > max) max = ni;
		}
		if (total % d.size == 0) {
			d.hasSolution = true;
			final int target = total / d.size;
			final int theoriticalSteps = max - target;

			int start=0, end=d.size-1;
			int[] previousStep = d.numbers;
			while (end - start > 0 && d.steps.size() < theoriticalSteps) {
				while (previousStep[start] == target && start < end) {
					start ++;
				}
				while (previousStep[end] == target && start < end) {
					end --;
				}

				if (end-start > 0) {
					int[] step = new int[d.size];
					System.arraycopy(previousStep, 0, step, 0, d.size);
					
					for (int i=start; i<=end; i++) {
						if (step[i] > target) {
							Direction dir = Direction.LEFT;
							if (i == start) {
								dir = Direction.RIGHT;
							}

							switch (dir) {
							case RIGHT:
								int j=i+1;
								do {
									step[j-1]--;
									step[j]++;
								} while (step[j++] > target);
								break;
							case LEFT:
								int k=i-1;
								do {
									step[k+1]--;
									step[k]++;
								} while (step[k--] > target);
								break;
							}

							if (i == start && step[i] == target) {
								start++;
							}

							if (i == end && step[i] == target) {
								end--;
							}
						}
					}

					d.steps.add(step);
					previousStep = step;
				}
			}
		}
	}

	enum Direction { LEFT, RIGHT }

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
			if (count > 0 && count <= 64) {
				Dataset d = new Dataset(count);
				this.datasets.add(d);
				buffer = br.readLine(); row ++;
				String[] datas = buffer.split(" ");
				if (datas.length != count) {
					throw new Exception("Nombre de points incohérent, ligne " + row);
				}
				for (int i=0; i<count; i++) {
					d.numbers[i] = Integer.parseInt(datas[i]);
					if (d.numbers[i] < 0 || d.numbers[i] > 99) {
						throw new Exception("Les entiers du vecteur doivent appartenir à la plage [0, 99], ligne " + row);
					}
				}
				br.readLine(); row ++;
			} else if (count != 0) {
				throw new Exception("Un vecteur doit être de taille comprise entre 1 et 64, ligne " + row);
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

