
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Balancer {

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

	public Balancer() throws Exception {
		readInput();
		ExecutorService pool = Executors.newFixedThreadPool(8);
		Collection<Future<?>> futures = new ArrayList<Future<?>>();
		counter.set(datasets.size());
		for (Dataset d : datasets) {
			futures.add(pool.submit(new Task(d)));
		}
		synchronized(counter) {
			int c = counter.get();
			while (c > 0) {
				c = counter.get();
				counter.wait(0, 10000);
			}
			pool.shutdownNow();
		}
	}
	
	AtomicInteger counter = new AtomicInteger();
	
	class Task implements Runnable {

		private Dataset d;
		
		public Task(Dataset d) {
			this.d = d;
		}

		@Override
		public void run() {
			balance(d);
			synchronized(counter) {
				counter.decrementAndGet();
				counter.notify();
			}
		}
		
		
	}

	/**
	 * <p>Implémentation de l'algorithme.</p>
	 * <p>Principe : parcourir de gauche à droite et propager un quantum vers la droite jusqu'à une case qui contient pas assez de quanta.</p>
	 * <p>La cible pour chaque case est la moyenne. Si la cible n'est pas un entier, alors il n'y a pas de solution.</p>
	 * <p>Si on arrive à droite on repart vers la gauche (approche optimiste).</p>
	 * <p>Le nombre d'étape est déterminé ainsi : max(vecteur) - cible.</p>
	 * <p>Optimisations : on ne parcourt pas tout le vecteur à chaque étape. L'idée consiste à amener à la valeur cible les cases des extrémités en priorité.
	 * Quand une extrémité est à la cible on ne la parcourt plus par la suite en rétrécissant la fenêtre bornée par les index start et end. Ces index sont mis à jour à chaque fois qu'on le peut cad si on
	 * met à jour une des extrémité de la fenêtre et qu'on l'amène à la valeur cible. La fenêtre peut donc bouger plusieurs fois par étape.
	 * </p>
	 * @param d
	 */
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

	private void print(PrintWriter w, int stepNumber, int[] nis) {
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
			new Balancer();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}

