package kr.go.kofiu.ctr.util;

import java.util.Hashtable;

import kr.go.kofiu.ctr.service.OutboxPollerJob;

import org.apache.log4j.Logger;



/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : FlatFile 에 대한 한글깨짐 및 거래국가코드 체크 
 * 작   성   자  : 김연수
 * 작   성   일  : 2009. 09. 30
 * copyright @ LG CNS. All Right Reserved
 * <pre>
 *******************************************************/

public class FlatFileValidity {
	
	
	Logger logger = Logger.getLogger(OutboxPollerJob.class);
	/**
	 * 유효성 체크(FIU 7차)
	 * @param String
	 */
	public Hashtable checkValidate(String[] tokens, String versionType){
		
		logger.info("checkValidate 실행됨_1.");		
		
	
		boolean validateCheck = true;
		Hashtable rtnHt = new Hashtable();
		
		
		String check_1	= tokens[7];	// 보고기관명
		String check_2	= tokens[9];	// 보고책임자명
		String check_3	= tokens[10];	// 보고담당자명
		String check_4	= "";	
		String check_5	= "";	
		// 서식 버전별로 국적코드 순서값이 틀리다.
		logger.info("versionType ["+versionType+"]");	
		if("088".equals(versionType)){
			check_4	= tokens[20];	// 국가코드(대문자 아파벳 이외에는 오류) 088		
			//logger.info("거래자 국적코드 ["+check_4+"] ");
		}else if("089".equals(versionType)){
			check_4	= tokens[15];	// 거래자 국가코드(대문자 아파벳 이외에는 오류) 089	15, 31
			check_5	= tokens[31];	// 대표자 국가코드(대문자 아파벳 이외에는 오류) 089	15, 31
			//logger.info("거래자 국적코드 ["+check_4+"] 대표자 국적코드 ["+check_5+"]");
		}
		
		
		
		
		// 1. 한글 유효설 체크
		logger.info("한글 유효성 체크 START ");				
		String[] strCk 		= new String[]{check_1,check_2,check_3};
		String[] strCkNm 	= new String[]{"보고기관명(7)","보고책임자명(9)","보고담당자명(10)"};
		int len = strCk.length;
		for(int i=0; i<len; i++){
			// 1_1. 한글영문 체크
			
			boolean d = specialPhraseCheck(strCk[i]);
			if( d == false){
				validateCheck = false;
				rtnHt.put("RESULT" ,	"N");
				rtnHt.put("ERRMSG" ,	"CTR보고문서의 "+strCkNm[i]+" 필드 값이 '"+strCk[i]+"' 입니다. 이는 한글 유효성 체크에 위배 됩니다.");
				break;
			}
			
			/*
			// 1_2. 특수문자 체크
			boolean d = specialPhraseCheck_2(strCk[i]);
			if( d == false){
				validateCheck = false;
				rtnHt.put("RESULT" ,	"N");
				rtnHt.put("ERRMSG" ,	"CTR보고문서의 "+strCkNm[i]+" 필드 값이 '"+strCk[i]+"' 입니다. 이는 한글 유효성 체크에 위배 됩니다.");
				break;
			}
			*/
		}
		
		//logger.info("한글 유효성 체크 END ["+validateCheck+"]");			
		if(validateCheck == false) return rtnHt;
		
		logger.info("거래국가코드 체크 START ");
		
		// 2. 거래국가코드 체크
		if("088".equals(versionType)  ){
			
			if( !"".equals(check_4)) {
				validateCheck = nationalCodeCheck(check_4);
			}
			if(validateCheck == false) {
				rtnHt.put("RESULT" ,	"N");
				rtnHt.put("ERRMSG" ,	"CTR보고문서의 거래자 국적코드  값이 '"+check_4+"' 입니다. 국적코드는 2글자의 대문자 알파벳 이어야 합니다.");			
				return rtnHt;
			}
			
		}else if("089".equals(versionType)  ){
			// 배열 15번째 : 거래자 국적코드(check_4) , 31번째 : 대표자 국적코드(check_5)
			// 거래자 국적코드 , 대표자 국적코드는  둘중 하나만 들어올수도 있고 다 들어올수도 있다

			
			if( !"".equals(check_4)  ){			// 대표자 국적코드 값만 있고, 값이 99가 아닐경우(대표자명이 반드시 존재한다는의미)
				validateCheck = nationalCodeCheck(check_4);
			}
			
			if( !"".equals(check_5)  ){			// 거래자 국적코드 값만 있고, 값이 99가 아닐경우(
				validateCheck = nationalCodeCheck(check_5);
			}
			
			
			if(validateCheck == false) {
				rtnHt.put("RESULT" ,	"N");
				rtnHt.put("ERRMSG" ,	"CTR보고문서의 거래자 국적코드 값이 ["+check_4+"] 대표자 국적코드 ["+check_5+"] 입니다. 국적코드는 2글자의 대문자 알파벳 이어야 합니다.");			
				return rtnHt;
			}			
			
/*			
			if( !"".equals(check_5)  ){			// 대표자 국적코드 값만 있고, 값이 99가 아닐경우(대표자명이 반드시 존재한다는의미)
				validateCheck = nationalCodeCheck(check_5);
			}else if(!"".equals(check_4) ){		// 거래자 국적코드 값만 있고, 값이 99가 아닐경우(
				validateCheck = nationalCodeCheck(check_4);
			//}else if( (!"".equals(check_4) && !"99".equals(check_4) ) && (!"".equals(check_5) && !"99".equals(check_5)) ){
			}else if( !"".equals(check_4)  && !"".equals(check_5)){				
				// (거래자국적코드 값이 있고 && 값이 99가 아닐경우 ) && (대표자국적코드 값이 있고 && 값이 99가 아닐경우 )
				
				boolean temp1 = nationalCodeCheck(check_4);
				boolean temp2 = nationalCodeCheck(check_5);
				if(temp1 == false || temp2 == false){
					validateCheck = false;
				}
				
			}
			
*/
			
			
		}

		logger.info("거래국가코드 체크 END ");		
		
		rtnHt.put("RESULT" ,	"Y");
		rtnHt.put("ERRMSG" ,	"");

		
		return rtnHt;
		
	}
	
	/**
	 * 한글, 영문 이외의 문자는 특수문자로 체크(FIU 7차)
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
	 * 정해진 특수문자로 체크(FIU 7차)
	 * @param String
	 */
	public boolean specialPhraseCheck_2(String ctrStr){
		// 기본적으로 처리할 특수문자(기본5개로 체크)
		String[] chkStr = new String[]{"%","?","^","&","□"};

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
	 * 국가코드 오류 체크(FIU 7차)
	 * 2글자의 대문자 알바멧만 사용 가능 (Ex : 정상 :KR , 오류 :  A1, kr, R2, '' .....) 
	 * @param String
	 */	
	public boolean nationalCodeCheck(String nationalCode){
	
		int nationalCodeLen = nationalCode.length();
		boolean nationalCodeCk = true;
	
	
		// 1. 국가코드 길이값 체크
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
		
	
		// 2. 대문자 알파벳 체크
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
