package com.festival.daemon;

public class Daemon {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long beginTime = System.currentTimeMillis();
		int caseInt = Integer.parseInt(args[0]);
		int[] dayIntAry = new int[caseInt];
		int[] teamIntAry = new int[caseInt];
		int[] payIntAry = new int[1000];
		float[] minResult = new float[caseInt];
		int i;
		int j;
		int cnt = 0;
		for(i = 0 ; i < caseInt ; i++ ){
			minResult[i] = -1;
			dayIntAry[i] = Integer.parseInt(args[i+1+cnt]);
			teamIntAry[i] = Integer.parseInt(args[i+2+cnt]);
//			System.out.println("dayIntAry["+i+"] = " + dayIntAry[i]);
//			System.out.println("teamIntAry["+i+"] = " + teamIntAry[i]);
			for(j = 0 ; j < dayIntAry[i] ; j++){
				payIntAry[j] = Integer.parseInt(args[i+3+j+cnt]);
//				System.out.println("payIntAry["+j+"] = " + payIntAry[j]);
			}
			cnt = j+1;
			int z;
			int y;
			int k;
			float totalTemp;
			for(z = teamIntAry[i] ; z <= dayIntAry[i] ; z++ ){
				for(k = 0 ; k <= dayIntAry[i]-z ; k++ ){
					totalTemp = 0;
					for(y = k ; y < z+k ; y++){
						totalTemp += payIntAry[y];
					}
					
//					System.out.println("totalTemp : " + totalTemp);
					float minTemp = totalTemp / z;
					if(minResult[i] == -1){
						minResult[i] = minTemp;
					}else if(minTemp < minResult[i]){
						minResult[i] = minTemp;
					}
//					System.out.println("minResult["+i+"] : " + minResult[i]);
				}
			}
		}
		
		int g;
		for(g = 0 ; g < caseInt ; g ++){
			System.out.println(minResult[g]);
		}
		long endTiem = System.currentTimeMillis();
		long ms = endTiem - beginTime;
		float sec = (float)ms/1000;
		float min = (float)sec/60;
		
		if(min > 1){
			System.out.println("����ð�: " + min + "(min)");
		} else if (sec > 1) {
		    System.out.println("����ð�: " + sec + "(sec)");
		} else {
		    System.out.println("����ð�: " + ms + "(ms)");
		}
	}

}
