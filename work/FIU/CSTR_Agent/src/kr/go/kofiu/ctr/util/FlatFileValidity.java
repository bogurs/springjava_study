package kr.go.kofiu.ctr.util;

import java.util.Hashtable;

import kr.go.kofiu.ctr.service.OutboxPollerJob;

import org.apache.log4j.Logger;



/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : FlatFile �� ���� �ѱ۱��� �� �ŷ������ڵ� üũ 
 * ��   ��   ��  : �迬��
 * ��   ��   ��  : 2009. 09. 30
 * copyright @ LG CNS. All Right Reserved
 * <pre>
 *******************************************************/

public class FlatFileValidity {
	
	
	Logger logger = Logger.getLogger(OutboxPollerJob.class);
	/**
	 * ��ȿ�� üũ(FIU 7��)
	 * @param String
	 */
	public Hashtable checkValidate(String[] tokens, String versionType){
		
		logger.info("checkValidate �����_1.");		
		
	
		boolean validateCheck = true;
		Hashtable rtnHt = new Hashtable();
		
		
		String check_1	= tokens[7];	// ��������
		String check_2	= tokens[9];	// ����å���ڸ�
		String check_3	= tokens[10];	// �������ڸ�
		String check_4	= "";	
		String check_5	= "";	
		// ���� �������� �����ڵ� �������� Ʋ����.
		logger.info("versionType ["+versionType+"]");	
		if("088".equals(versionType)){
			check_4	= tokens[20];	// �����ڵ�(�빮�� ���ĺ� �̿ܿ��� ����) 088		
			//logger.info("�ŷ��� �����ڵ� ["+check_4+"] ");
		}else if("089".equals(versionType)){
			check_4	= tokens[15];	// �ŷ��� �����ڵ�(�빮�� ���ĺ� �̿ܿ��� ����) 089	15, 31
			check_5	= tokens[31];	// ��ǥ�� �����ڵ�(�빮�� ���ĺ� �̿ܿ��� ����) 089	15, 31
			//logger.info("�ŷ��� �����ڵ� ["+check_4+"] ��ǥ�� �����ڵ� ["+check_5+"]");
		}
		
		
		
		
		// 1. �ѱ� ��ȿ�� üũ
		logger.info("�ѱ� ��ȿ�� üũ START ");				
		String[] strCk 		= new String[]{check_1,check_2,check_3};
		String[] strCkNm 	= new String[]{"��������(7)","����å���ڸ�(9)","�������ڸ�(10)"};
		int len = strCk.length;
		for(int i=0; i<len; i++){
			// 1_1. �ѱۿ��� üũ
			
			boolean d = specialPhraseCheck(strCk[i]);
			if( d == false){
				validateCheck = false;
				rtnHt.put("RESULT" ,	"N");
				rtnHt.put("ERRMSG" ,	"CTR�������� "+strCkNm[i]+" �ʵ� ���� '"+strCk[i]+"' �Դϴ�. �̴� �ѱ� ��ȿ�� üũ�� ���� �˴ϴ�.");
				break;
			}
			
			/*
			// 1_2. Ư������ üũ
			boolean d = specialPhraseCheck_2(strCk[i]);
			if( d == false){
				validateCheck = false;
				rtnHt.put("RESULT" ,	"N");
				rtnHt.put("ERRMSG" ,	"CTR�������� "+strCkNm[i]+" �ʵ� ���� '"+strCk[i]+"' �Դϴ�. �̴� �ѱ� ��ȿ�� üũ�� ���� �˴ϴ�.");
				break;
			}
			*/
		}
		
		//logger.info("�ѱ� ��ȿ�� üũ END ["+validateCheck+"]");			
		if(validateCheck == false) return rtnHt;
		
		logger.info("�ŷ������ڵ� üũ START ");
		
		// 2. �ŷ������ڵ� üũ
		if("088".equals(versionType)  ){
			
			if( !"".equals(check_4)) {
				validateCheck = nationalCodeCheck(check_4);
			}
			if(validateCheck == false) {
				rtnHt.put("RESULT" ,	"N");
				rtnHt.put("ERRMSG" ,	"CTR�������� �ŷ��� �����ڵ�  ���� '"+check_4+"' �Դϴ�. �����ڵ�� 2������ �빮�� ���ĺ� �̾�� �մϴ�.");			
				return rtnHt;
			}
			
		}else if("089".equals(versionType)  ){
			// �迭 15��° : �ŷ��� �����ڵ�(check_4) , 31��° : ��ǥ�� �����ڵ�(check_5)
			// �ŷ��� �����ڵ� , ��ǥ�� �����ڵ��  ���� �ϳ��� ���ü��� �ְ� �� ���ü��� �ִ�

			
			if( !"".equals(check_4)  ){			// ��ǥ�� �����ڵ� ���� �ְ�, ���� 99�� �ƴҰ��(��ǥ�ڸ��� �ݵ�� �����Ѵٴ��ǹ�)
				validateCheck = nationalCodeCheck(check_4);
			}
			
			if( !"".equals(check_5)  ){			// �ŷ��� �����ڵ� ���� �ְ�, ���� 99�� �ƴҰ��(
				validateCheck = nationalCodeCheck(check_5);
			}
			
			
			if(validateCheck == false) {
				rtnHt.put("RESULT" ,	"N");
				rtnHt.put("ERRMSG" ,	"CTR�������� �ŷ��� �����ڵ� ���� ["+check_4+"] ��ǥ�� �����ڵ� ["+check_5+"] �Դϴ�. �����ڵ�� 2������ �빮�� ���ĺ� �̾�� �մϴ�.");			
				return rtnHt;
			}			
			
/*			
			if( !"".equals(check_5)  ){			// ��ǥ�� �����ڵ� ���� �ְ�, ���� 99�� �ƴҰ��(��ǥ�ڸ��� �ݵ�� �����Ѵٴ��ǹ�)
				validateCheck = nationalCodeCheck(check_5);
			}else if(!"".equals(check_4) ){		// �ŷ��� �����ڵ� ���� �ְ�, ���� 99�� �ƴҰ��(
				validateCheck = nationalCodeCheck(check_4);
			//}else if( (!"".equals(check_4) && !"99".equals(check_4) ) && (!"".equals(check_5) && !"99".equals(check_5)) ){
			}else if( !"".equals(check_4)  && !"".equals(check_5)){				
				// (�ŷ��ڱ����ڵ� ���� �ְ� && ���� 99�� �ƴҰ�� ) && (��ǥ�ڱ����ڵ� ���� �ְ� && ���� 99�� �ƴҰ�� )
				
				boolean temp1 = nationalCodeCheck(check_4);
				boolean temp2 = nationalCodeCheck(check_5);
				if(temp1 == false || temp2 == false){
					validateCheck = false;
				}
				
			}
			
*/
			
			
		}

		logger.info("�ŷ������ڵ� üũ END ");		
		
		rtnHt.put("RESULT" ,	"Y");
		rtnHt.put("ERRMSG" ,	"");

		
		return rtnHt;
		
	}
	
	/**
	 * �ѱ�, ���� �̿��� ���ڴ� Ư�����ڷ� üũ(FIU 7��)
	 * @param String
	 */	
	public boolean specialPhraseCheck(String s) {
/*
        int strLen = s.length();
		boolean check = false;
        for (int i = 0 ; i < strLen ; i++) {
            int j = (int)s.charAt(i);
            // 128 : Default Ascii Code , 256 : Extended Ascii Code   
			if (j > 128 || (j >=65 && j<=90) || (j>=97 && j<=122) ){
				check=true;
			}else{
				check=false;
				break;
			}
        }

        return check;
*/
		
        int strLen = s.length();
		boolean check = true;
        for (int i = 0 ; i < strLen ; i++) {
            int j = (int)s.charAt(i);
			// 63 : ? , 
			if(j == 63 || (j>=128 && j <= 255) ){
				check=false;
				break;
			}
			
        }

        return check;
        
    }
	
	
	/**
	 * ������ Ư�����ڷ� üũ(FIU 7��)
	 * @param String
	 */
	public boolean specialPhraseCheck_2(String ctrStr){
		// �⺻������ ó���� Ư������(�⺻5���� üũ)
		String[] chkStr = new String[]{"%","?","^","&","��"};

        int ctrLen = ctrStr.length();
		int chkLen = chkStr.length;
        

		boolean check = true;
        for (int i = 0 ; i < ctrLen ; i++) {
			String tem = ctrStr.substring(i,i+1);
			
			for(int k=0; k<chkLen; k++){
				if(tem.equals(chkStr[k])){
					check = false;
					break;
				}
			}
			if (check == false){
				break;
			}
        }
        return check;
	}
	


	/**
	 * �����ڵ� ���� üũ(FIU 7��)
	 * 2������ �빮�� �˹ٸ丸 ��� ���� (Ex : ���� :KR , ���� :  A1, kr, R2, '' .....) 
	 * @param String
	 */	
	public boolean nationalCodeCheck(String nationalCode){
	
		int nationalCodeLen = nationalCode.length();
		boolean nationalCodeCk = true;
	
	
		// 1. �����ڵ� ���̰� üũ
		if(nationalCodeLen != 2){
			nationalCodeCk = false;
			
		}
		
		if("99".equals(nationalCode)||"A1".equals(nationalCode)
			||"A2".equals(nationalCode)||"A3".equals(nationalCode)||"B1".equals(nationalCode)
			||"B2".equals(nationalCode)||"C1".equals(nationalCode)||"C2".equals(nationalCode)
			||"C2".equals(nationalCode)||"C3".equals(nationalCode)||"E1".equals(nationalCode)
			||"E2".equals(nationalCode)||"E3".equals(nationalCode)
			){
			return true;
		}
		
	
		// 2. �빮�� ���ĺ� üũ
		for (int i = 0 ; i < nationalCodeLen ; i++) {
			int j = (int)nationalCode.charAt(i);
			if ( (j >=65 && j<=90) ){
				continue;
			}else{
				nationalCodeCk = false;
				break;
			}
	
		}
	
		return nationalCodeCk;
	
	}

	public static void main(String[] args) {
		
	}

}
