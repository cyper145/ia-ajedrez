import java.util.Arrays;


public class Sort {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] a = {8,3,2,3,4,5,6};
		System.out.println(Arrays.toString(a));
		insertionSort(a);
		System.out.println(Arrays.toString(a));
	}
	
	
	public static void insertionSort(int[] agentes){
		int value;
		int j=0;
		for (int i = 1; i < agentes.length; i++) {
			value = agentes[i];
			j = i-1;
			while(j>=0 && agentes[j]<value){
				agentes[j+1] = agentes[j];
				j--;				
			}
			agentes[j+1] = value;
		}
	}
	
	public static void insertionSort(IDSAgent[] agentes){
		IDSAgent value;
		int j=0;
		for (int i = 1; i < agentes.length; i++) {
			value = agentes[i];
			j = i-1;
			while(j>=0 && agentes[j].wins<value.wins){
				agentes[j+1] = agentes[j];
				j--;				
			}
			agentes[j+1] = value;
		}
	}
	
	
	
	public static void insertionSort(Agent[] agentes){
		Agent value;
		int j=0;
		for (int i = 1; i < agentes.length; i++) {
			value = agentes[i];
			j = i-1;
			while(j>=0 && agentes[j].wins<value.wins){
				agentes[j+1] = agentes[j];
				j--;				
			}
			agentes[j+1] = value;
		}
	}
}
