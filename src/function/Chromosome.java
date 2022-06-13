package function;

import java.util.Random;

public class Chromosome {
	public int [] binaryRepresetation; // binarna vrednost x
	public double decimalValueOfX; // decimalna (prava) vrednost x eg ( 9 / 15)
	public double fitnessFunctionValue; // vrednost fitnes funkcije za zadato x
	public Random r = new Random();
	public StringBuilder binaryNumber = new StringBuilder(); //String reprezentacija binarne vrednosti
	public Chromosome(int binaryArrayLength) {
		binaryRepresetation = new int[binaryArrayLength];
		fillBinaryRepresetation(binaryRepresetation);
		calculateValues();
	}

	public double fitness_function(Double chromosomeX) {

		return (Math.pow(Math.sin(10 * chromosomeX), 2)) / (1 + chromosomeX);
	}
	public void calculateValues() {
		decimalValueOfX = Integer.parseInt(binaryNumber.toString(),2)/(Math.pow(2,this.binaryRepresetation.length)-1);
		fitnessFunctionValue = fitness_function(decimalValueOfX);
	}
	public void fillBinaryRepresetation(int [] binaryRepresetation){
		binaryNumber = new StringBuilder();
		binaryRepresetation = this.binaryRepresetation;
		for(int i = 0; i < binaryRepresetation.length; i++){
			int random = r.nextInt(2);
			binaryRepresetation[i] = random;
			binaryNumber.append(random);
		}
	}

	public void resetBinaryNumber(){
		binaryNumber = new StringBuilder();
		binaryRepresetation = this.binaryRepresetation;
		for(int i = 0; i < binaryRepresetation.length; i++){
			binaryNumber.append(binaryRepresetation[i]);
		}
	}
	}
