package emulator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.*;

public class emuInJava {
	static int reg[]=new int[16];
	static int flags[]=new int[2];
	static int memory[]=new int[1024];
	static String labels[];
	static int labStartEnd[][];
	static int count=0;
	
	public static void main(String[] args) {
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter("output.txt"));
			String content = new String(Files.readAllBytes(Paths.get("factorial2.asm")));
			String inst[]=content.split("\n");
			/*for(int i=0;i<inst.length;i++) {
				System.out.println(i+" :  "+inst[i]);
			}*/
			labels=new String[inst.length];
			int labStartEnd[][]=new int[inst.length][2];
			int c1,c2,mainLabelNum=-1;
			for(int i=0;i<inst.length;i++) {
				c1=inst[i].indexOf('.');
				c2=inst[i].indexOf(':');
				if(c1>=0 && c2>=0) {
					
					labels[count]=inst[i].substring(c1+1,c2);
					//System.out.println(labels[count]);
					if(labels[count].equals("main")) {
						mainLabelNum=count;
					}
					labStartEnd[count][0]=i;
					int d1=inst[i].lastIndexOf('\t')+1;
					int copyI=i;				
					while(true) {
						int e1=inst[++copyI].lastIndexOf('\t')+1;
						if(e1<=d1 || copyI>=inst.length-1) {
							break;
						}						
					}
					if(copyI>=inst.length-1)
						labStartEnd[count][1]=copyI;
					else
						labStartEnd[count][1]=copyI-1;
					count++;
				}
			}
			int retCount=0;
			for(int i=labStartEnd[mainLabelNum][0];i<=labStartEnd[mainLabelNum][1];i++) {
				int val;
				if(inst[i].contains(".encode")) {
					inst[i]=inst[i].substring(inst[i].indexOf(' ')+1, inst[i].length());
					val=executeInst(inst[i],i, labStartEnd, 1, writer);
				}
				else {
					val=executeInst(inst[i], i, labStartEnd, 0, writer);
				}
				if(inst[i].contains("ret") && retCount<1) {
					i=val;
					retCount++;
				}
				if(inst[i].indexOf('.')>=0) {
					if(inst[i].indexOf(':')<0 && val!=-999) {
						i=labStartEnd[val][0];
					}
				}
			}
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int executeInst(String ins,int lineNum, int[][] labStartEnd, int encodeFlag, BufferedWriter writer) throws FileNotFoundException {
		
		String encode="";
		String binaryCodes[]= {"0000","0001","0010","0011","0100","0101","0110","0111","1000",
				"1001","1010","1011","1100","1101","1110","1111"};
		if(ins.indexOf(':')<0) {
			ins=ins.trim();
			String opcode,rest="",regs[]=new String[4];
			if(ins.contains("ret")) {
				opcode="ret";
			}
			else {
				opcode=ins.substring(0,ins.indexOf(' '));
				rest=ins.substring(ins.indexOf(' ')+1,ins.length());
				regs=rest.split(",");
				for(int i=0;i<regs.length;i++) {
					regs[i]=regs[i].trim();
				}
			}
			
			
			switch (opcode) {
			case "add":
				encode+="00000";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]+Integer.parseInt(regs[2]);
				}
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]+reg[((int)regs[2].charAt(1))-48];
				}
				
				if(encodeFlag==1) {
					System.out.println(encode);
					try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				return lineNum+1;
							
			case "sub":
				encode+="00001";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]-Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]-reg[((int)regs[2].charAt(1))-48];
				}	
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "mul":
				encode+="00010";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]*Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]*reg[((int)regs[2].charAt(1))-48];
				}	
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;

			case "div":
				encode+="00011";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]/Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]/reg[((int)regs[2].charAt(1))-48];
				}	
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "mod":
				encode+="00100";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]%Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]%reg[((int)regs[2].charAt(1))-48];
				}
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "cmp":
				encode+="00101";
				if(isNumeric(regs[1])) {
					encode+="1"+"xxxx"+binaryCodes[((int)regs[0].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[1]), 16);
					
					if(reg[((int)regs[0].charAt(1))-48]>Integer.parseInt(regs[1])) {
						flags[0]=0;
						flags[1]=1;
					}
					else if(reg[((int)regs[0].charAt(1))-48]==Integer.parseInt(regs[1])) {
						flags[0]=1;
						flags[1]=0;
					}
					else {
						flags[0]=0;
						flags[1]=0;
					}	
				}
				else {
					encode+="0"+"xxxx"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"xxxxxxxxxxxxxx";
					
					if(reg[((int)regs[0].charAt(1))-48]>reg[((int)regs[1].charAt(1))-48]) {
						flags[0]=0;
						flags[1]=1;
					}
					else if(reg[((int)regs[0].charAt(1))-48]==reg[((int)regs[1].charAt(1))-48]) {
						flags[0]=1;
						flags[1]=0;
					}
					else {
						flags[0]=0;
						flags[1]=0;
					}	
				}
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "and":
				encode+="00110";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]&Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]&reg[((int)regs[2].charAt(1))-48];
				}	
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "or":
				encode+="00111";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]|Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]|reg[((int)regs[2].charAt(1))-48];
				}	
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "not":
				encode+="01000";
				if(isNumeric(regs[1])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+"xxxx"
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=~Integer.parseInt(regs[1]);
				}
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+"xxxx"+binaryCodes[((int)regs[1].charAt(1))-48]
							+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=~reg[((int)regs[1].charAt(1))-48];
				}
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "mov":
				encode+="01001";
				if(isNumeric(regs[1])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+"xxxx"
							+"00"+giveBinaryConvert(Integer.parseInt(regs[1]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=Integer.parseInt(regs[1]);
				}
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+"xxxx"+binaryCodes[((int)regs[1].charAt(1))-48]
							+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48];
				}
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "lsl":
				encode+="01010";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]<<Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]<<reg[((int)regs[2].charAt(1))-48];
				}	
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "lsr":
				encode+="01011";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]>>>Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]>>>reg[((int)regs[2].charAt(1))-48];
				}	
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "asr":
				encode+="01100";
				if(isNumeric(regs[2])) {
					encode+="1"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+"00"+giveBinaryConvert(Integer.parseInt(regs[2]), 16);
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]>>Integer.parseInt(regs[2]);
				}					
				else {
					encode+="0"+binaryCodes[((int)regs[0].charAt(1))-48]+binaryCodes[((int)regs[1].charAt(1))-48]
							+binaryCodes[((int)regs[2].charAt(1))-48]+"xxxxxxxxxxxxxx";
					
					reg[((int)regs[0].charAt(1))-48]=reg[((int)regs[1].charAt(1))-48]>>reg[((int)regs[2].charAt(1))-48];
				}
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return lineNum+1;
				
			case "nop":
				encode+="01101xxxxxxxxxxxxxxxxxxxxxxxxxxx";
				// do nothing
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
				
			case "ld":
				if(regs[1].contains("H")) {
					regs[1]=regs[1].substring(0,4);
					reg[((int)regs[0].charAt(1))-48]=memory[Integer.parseInt(regs[1])] ;
				}
				else if(regs[1].contains("[")) {
					String imm=regs[1].substring(0,regs[1].indexOf('['));
					int val=0;
					if(imm!="") {
						val=Integer.parseInt(imm);
					}
					regs[1]=regs[1].substring(regs[1].indexOf('[')+2,regs[1].indexOf(']'));
					reg[((int)regs[0].charAt(1))-48]=reg[(Integer.parseInt(regs[1]))+val];
				}
				break;
				
			case "st":
				if(regs[1].contains("H")) {
					regs[1]=regs[1].substring(0,4);
					memory[Integer.parseInt(regs[1])]=reg[((int)regs[0].charAt(1))-48] ;
				}
				else if(regs[1].contains("[")) {
					String imm=regs[1].substring(0,regs[1].indexOf('['));
					int val=0;
					if(imm!="") {
						val=Integer.parseInt(imm);
					}
					regs[1]=regs[1].substring(regs[1].indexOf('[')+2,regs[1].indexOf(']'));
					reg[(Integer.parseInt(regs[1]))+val]=reg[((int)regs[0].charAt(1))-48];
				}
				break;
				
			case "beq":
				if(flags[0]==1) {
					String s=regs[0].substring(rest.indexOf('.')+1).trim();
					for(int i=0;i<count;i++) {
						if(s.equals(labels[i])) {
							encode+="10000"+giveBinaryConvert(labStartEnd[i][0], 27);
							if(encodeFlag==1) {
								System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
							}
							return i;
						}
					}
				}
				else 
					return -999;
				
			case "bgt":
				if(flags[1]==1) {
					String s=regs[0].substring(rest.indexOf('.')+1).trim();
					for(int i=0;i<count;i++) {
						if(s.equals(labels[i])) {
							encode+="10001"+giveBinaryConvert(labStartEnd[i][0], 27);
							if(encodeFlag==1) {
								System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
							}
							return i;
						}
							
					}
				}
				else 
					return -999;
				
			case "b":
				String s=regs[0].substring(rest.indexOf('.')+1).trim();
				for(int i=0;i<count;i++) {
					if(s.equals(labels[i])) {
						encode+="10010"+giveBinaryConvert(labStartEnd[i][0], 27);
						if(encodeFlag==1) {
							System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
						}
						return i;
					}
						
				}
				return -999;
				
			case "call":
				reg[15]=lineNum;
				String labelName=regs[0].substring(rest.indexOf('.')+1).trim();
				int posLabel;
				for(int i=0;i<count;i++) {
					if(labelName.equals(labels[i])) {
						encode+="10011";
						String addInst=Integer.toBinaryString(labStartEnd[i][0]);
						int strLen=addInst.length();
						for(int z=1;z<=(27-strLen);z++)
							encode+="0";
						encode+=addInst;
						if(encodeFlag==1) {
							System.out.println(encode);try {
						writer.write(encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
						}
						return i;
					}
						
				}
				return -999;
				
			case "ret":
				encode+="10100xxxxxxxxxxxxxxxxxxxxxxxxxxx";
				if(encodeFlag==1) {
					System.out.println(encode);try {
						writer.write("\n"+encode+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return reg[15];
				
			case ".print":
				System.out.println("r"+(((int)regs[0].charAt(1))-48)+" = "+reg[((int)regs[0].charAt(1))-48]);
				try {
					writer.write("r"+(((int)regs[0].charAt(1))-48)+" = "+reg[((int)regs[0].charAt(1))-48]+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return -999;
				
			case ".encode":
				return 1;
							
			default:
				return -999;
			}
		}
		
		return -999;
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {
	    int d = Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {
	    return false;  
	  }  
	  return true;  
	}
	
	public static StringBuffer giveBinaryConvert(int n, int numBits) {
		StringBuffer binaryString=new StringBuffer();
		binaryString.append(Integer.toBinaryString(n));
		for(int i=binaryString.length(); i<numBits; i++) {
			binaryString.insert(0, "0");
		}
		return binaryString;
	}

}
