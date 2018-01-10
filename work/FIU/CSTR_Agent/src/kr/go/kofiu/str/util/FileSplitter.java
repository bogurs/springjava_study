package kr.go.kofiu.str.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileSplitter {
	private File srcFile;
    private String destDir;
    private int maxseq;
     
    public FileSplitter(File srcFile, String destDir) {
        this.srcFile= srcFile;      
        this.destDir= destDir;
    }
    
    /**
     * 파일을 분할하여 적재한다.
     * 
     * @param preferedSize 분할파일 사이즈
     * @return
     */
    public String split(long preferedSize) {
        String fileName= srcFile.getName();
         
        long srcSize= srcFile.length();     
        System.out.println("srcSize = " + srcSize);
        long quotient= srcSize/preferedSize;
        System.out.println("quotient = " + quotient);
        int fileNoSpace= getFileNumberSpace(quotient+1);
        System.out.println("fileNoSpace = " + fileNoSpace);
         
        int fileNo= 0;      
                     
        int buffSize= 9096;
         
        try {
            String firstFileName= destDir+formatFileName(fileName, fileNo, fileNoSpace); 
            FileOutputStream fos= new FileOutputStream(new File(firstFileName));
            System.out.println(firstFileName);
            OutputStream os= new BufferedOutputStream(fos);
                                     
            FileInputStream fis= new FileInputStream(srcFile);
            InputStream is= new BufferedInputStream(fis);
             
            byte buf[] = new byte[buffSize];
            int s = 0;
            long writtenSize= 0;
     
            while ( (s = is.read(buf, 0, buffSize)) > 0 ) {
                if (writtenSize > preferedSize) {
                    writtenSize=0;
                    fileNo++;                   
                    os.close();
                    String outputFileName= destDir+formatFileName(fileName, fileNo, fileNoSpace);
                    System.out.println(outputFileName);
                    os= new BufferedOutputStream(new FileOutputStream(new File(outputFileName)));
                     
                }
                os.write(buf, 0, s);
                writtenSize= writtenSize + s;
            }
            maxseq = fileNo;
            fos.close();
            os.close();
            is.close();
             
             
             
        } catch (FileNotFoundException e) {
	       return "분할 하려는 PROC 파일을 찾을 수 없습니다.";
        } catch (IOException e) {
        	return "파일을 나누는 과정에서 입출력 에러가 발생했습니다.";
        }  
        return "OK";
    }
     
    private int getFileNumberSpace(long quotient) {
        if (quotient==0) {
            return 1;
        }
         
        int space= 0;
        while( quotient > 0) {
            quotient= quotient/10;
            space++;
        }
        return space;
    }
     
    private String formatFileName(String prefix, int fileNo, int fileNoSpace) {
              
        StringBuffer sb= new StringBuffer();
         
        sb.append(prefix+".part");
        
        sb.append(fileNo);
         
        return sb.toString();
    }

	public int getMaxseq() {
		return maxseq;
	}

	public void setMaxseq(int maxseq) {
		this.maxseq = maxseq;
	}
    
    
}
