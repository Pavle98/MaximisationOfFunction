package levi13;

import java.util.ArrayList;
import java.util.Random;


public class Main {
	static int generationNumber = 250;    //maximum number of experiment repetitions
	public static int maxIterations = 500; //maximum number of generations in one repetition
	static double mutationRate = 0.25;
	static double crossRate = 0.5;
	static int populationNumber = 30;
	static ArrayList<Chromosome> population = new ArrayList<>();
	static public  Random r = new Random();
	public static void main(String[] args) {

		//Inicijalizujemo pocetnu populaciju
			initialization(populationNumber, 20);



		//Ispis prve generacije, ispisujemo x i y, tacnije vrednost varijable x i rezultat fitnes funkcije sa tim x
		for(int i = 0; i < populationNumber; i++){
			System.out.println(  "(" + population.get(i).decimalValueOfX +"," + population.get(i).fitnessFunctionValue + ")");
		}

			//prolazimo odredjeni broj puta kroz ovu petlju (u ovom slucaju 100) (osim ako se ne ispuni dole navedeni uslov), i tako "evoluiramo" nasu populaciju
			//prvo vrsimo selekciju, pa ukrstanje pa mutaciju
			for(int i = 0; i < generationNumber; i++) {

				population = selection(population); //selekcija

				population = crossover(population, crossRate); //ukrstanje sa verovatnocom da ce se desiti od 0.5
				population= mutate(population, mutationRate);//mutacija sa verovatnocom da ce se desiti od 0.25

				if(calculateAllFitneses(population) > 24){
					//Uslov koji kaze da ako ukupna fitness vrednost predje odredjenu vrednost
					// (u ovom slucaju 24), prekinemo evoluciju jer smo nasli dovoljno pribliznu vrednost (idealna je oko 25.977 u nasem slucaju sa 30 jedinki)
					System.out.println("we are happy with results" + " stopped at " + i + " iteration ");
					break;
				}
			}

		//Ispis poslednje generacije
		System.out.println();
		for(int i = 0; i <populationNumber; i++){
			System.out.println(  "(" + population.get(i).decimalValueOfX +"," + population.get(i).fitnessFunctionValue + ")");
		}
	}

	public static void initialization(int populationNumber, int binaryArrayLength) {
		//pravimo pocetnu populaciju gde prosledjujemo samo duzinu binarnog niza (Ako bismo hromozom gledali kao matricu ovo je prvih L kolona)
		//A u konstruktoru hromozoma se dodeljuju random vrednosti (0 ili 1) za svakog clana tog niza,
		// takodje se i racuna racuna vrednost pravog X (decimalValueOfX) i fitnes funkcije (fitnessFunctionValue)(U matrici bi ovo bilo L+1 I L+2)
	 for(int i = 0; i < populationNumber; i++){
			population.add(new Chromosome(binaryArrayLength));
	 }

	}



	public static ArrayList<Chromosome> mutate(ArrayList<Chromosome> population, double mut_rate) {
		//Pravimo listu koju cemo popunjavati sa mutiranim clanovima prethodne generacije
		ArrayList<Chromosome> newPopulation = new ArrayList<>();
		int binaryLength = population.get(0).binaryRepresetation.length; //Ovo nam je potrebno zbog random funkcije dole u kodu
		//Prolazimo kroz svaki hromozom u populaciji i ukoliko je random vrednost koju generisemo veca od sanse za mutaciju (mut_rate),
		//mutiramo hromozom i stavljamo ga u novu populaciju, ukoliko nije vec, ne mutiramo ga vec ga stavljamo takvog kakav jeste u novu populaciju
		for(Chromosome c : population) {
			//pravimo novi hromozom kome cemo posle proslediti binarni niz originalnog cvora,
			//mutirati ga tako sto cemo promeniti jedan bit u tom nizu i ponovo izracunati vrednosti za
			//X i fitnes funkciju.
			Chromosome mutedChrom = new Chromosome(c.binaryRepresetation.length);
			double rand = (Math.random());
			if (rand < mut_rate) {
				int randomBit = r.nextInt(binaryLength);
				//mutiranom hromozomu prosledjujemo binarni niz originalnog cvora
				mutedChrom.binaryRepresetation = c.binaryRepresetation;
				//menjamo jedan bit
				mutedChrom.binaryRepresetation[randomBit] = Math.abs((c.binaryRepresetation[randomBit]-1));
				//binaryNumber je binarni niz (binaryRepresetation) predstavljen u vidu stringa, to nam je bilo potrebno da izracunamo x, zato i tu menjamo taj bit
				mutedChrom.binaryNumber.setCharAt(randomBit, c.binaryNumber.charAt(randomBit));
				//ponovo izracunavamo x i fitness vrednost funkcije
				mutedChrom.calculateValues();

				newPopulation.add(mutedChrom);

			}else{
				newPopulation.add(c);
			}
		}
		return newPopulation;
	}

	//
	public static ArrayList<Chromosome> selection(ArrayList<Chromosome>population) {
		//Selekcija na osnovu proporcije fitnesa ili (Selekcija tocka ruleta)
		double totalFitnessSum = calculateAllFitneses(population);// Dodeljujemo sumu rezultata vrednosti fitnes funkcije za svakog hromozoma u populaciji
		int newPopSize = 0;
		ArrayList<Chromosome> newPopulation = new ArrayList<>();
		double randomValue; // random vrednost koju cemo da uporedjujemo sa parcijalnom vrednoscu i tako odluciti koji hromozom ide u novu populaciju
		double partialSum = 0;
		while(newPopSize < population.size()) {
			randomValue = totalFitnessSum * r.nextDouble(); //incijalizujemo random vrednost izmedju 0 i sume svih fitnes funkcija
			for (Chromosome c : population) {
				//Sabiramo vrednosti fitnes funkcije za svaki hromozom, stavljamo u partialSum varijablu i to  poredimo sa randomValue
				//kada partialSum preraste tu random vrednost, taj hromozom do kog smo stigli u petlji ide u novu populaciju
				// time omogucavamo da hromozom sa najvecom vrednoscu ima najvecu sansu da ide u novu populaciju
				//To cemo raditi sve dok nova populacija nema isti broj jedinki kao stara, zbog toga se moze desiti da
				//ista jedinka se pojavi vise puta u novoj generaciji
				partialSum += c.fitnessFunctionValue;
				if (partialSum >= randomValue) {
					newPopulation.add(c);
					newPopSize++;
					partialSum = 0;
					break;
				}
			}
		}
		return newPopulation;
	}

//kako resiti neparan broj u populaciji
	public static ArrayList<Chromosome> crossover(ArrayList<Chromosome> population, double cross_rate) {
		//one point crossover
		double rand = (Math.random());
		int binaryLength = population.get(0).binaryRepresetation.length;

		ArrayList<Chromosome> newPopulation = new ArrayList<>();

			//zato sto cemo u svakoj iteraciji dobijati dvoje dece u novoj generaciji, imacemo (velicinaPopulacije / 2) iteracija
		for(int newPop = 0; newPop < population.size()/2; newPop++) {
			//Uzimamo dva random roditelja iz populacije i pravimo dvoje dece kojima cemo posle dodeliti podatke roditelja
			Chromosome firstParent = population.get(r.nextInt(population.size()));
			Chromosome secondParent = population.get(r.nextInt(population.size()));
			Chromosome firstChild = new Chromosome(binaryLength);
			Chromosome secondChild = new Chromosome(binaryLength);

			//Isto kao i za mutaciju, ako random generisani broj bude veci od sanse za ukrstanje
			//menjamo binarni kod dece, a ako ne bude, dodeljujemo im isti kao kod roditelja
			if (rand < cross_rate) {
				//odredjujemo randomBit koji oznacava poziciju tacke ukrstanja,
				//potom ulazimo u for petlju gde prolazimo kroz svaki bit u binarnom kodu
				//u zavisnosti od toga da li je pozicija bita veci ili manja od tacke ukrstanja
				//odredjujemo od kog ce roditelja preuzeti bit

				int randomBit = r.nextInt(binaryLength);
				for (int i = 0; i < binaryLength; i++) {
					if (i < randomBit) {
						//ukoliko je i manje od tacke ukrstanja, prvo dete uzima od prvog roditelja,drugo dete od drugog
						//ukoliko je i vece, obrnuto je
						firstChild.binaryRepresetation[i] = firstParent.binaryRepresetation[i];
						secondChild.binaryRepresetation[i] = secondParent.binaryRepresetation[i];

					} else {
						firstChild.binaryRepresetation[i] = secondParent.binaryRepresetation[i];
						secondChild.binaryRepresetation[i] = firstParent.binaryRepresetation[i];
					}
				}
			} else {
				firstChild = firstParent;
				secondChild = secondParent;
			}

			//dodajemo decu u novu generaciju
		newPopulation.add(firstChild);
		newPopulation.add(secondChild);
		}
		//prolazimo kroz celu novu generaciju i ponovo pravimo od binarnog niza string vrednost (resetBinaryNumber)
		// i racunamo im vrednosti za X i fitnes funkciju (calculateValues)
		for (Chromosome c : newPopulation){
			c.resetBinaryNumber();
			c.calculateValues();
		}
		return newPopulation;
	}
	public static double calculateAllFitneses(ArrayList<Chromosome> population){
		double totalFitness = 0;
		for(Chromosome c : population){
			totalFitness += c.fitnessFunctionValue;
		}
		return totalFitness;
	}


}
