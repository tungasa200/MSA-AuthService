package com.yjmedia.yvisbig.bizcom.util;


public class AnsiX923Padding implements CryptoPadding {
	
	
	private String name = "ANSI-X.923-Padding";
	
	private final byte PADDING_VALUE = (byte)0x80;
	
	
	public byte[] addPadding(byte[] source, int blockSize) {
		int paddingCnt = source.length % blockSize;
		byte[] paddingResult = null;
		
		if(paddingCnt != 0) {
			paddingResult = new byte[source.length + (blockSize - paddingCnt)];
			
			System.arraycopy(source, 0, paddingResult, 0, source.length);			
			
			int addPaddingCnt = blockSize - paddingCnt;
			//System.out.println("paddingCNT :: " + String.valueOf(addPaddingCnt));
			//System.out.println("PADDING_VALUE :: " + Integer.toHexString(0xff & PADDING_VALUE));
			for(int i=0;i<addPaddingCnt;i++) {
				paddingResult[source.length + i] = PADDING_VALUE;
			}
			
			
			paddingResult[paddingResult.length - 1] = (byte)addPaddingCnt;			
		} else {
			paddingResult = source;
		}

		return paddingResult;
	}

	public byte[] add80Padding(byte[] source, int blockSize) {
		int paddingCnt = source.length % blockSize;
		byte[] paddingResult = null;		
		if(paddingCnt != 0) {
			paddingResult = new byte[source.length + (blockSize - paddingCnt)];
			
			System.arraycopy(source, 0, paddingResult, 0, source.length);
			
			
			int addPaddingCnt = blockSize - paddingCnt;
			for(int i=0;i<addPaddingCnt;i++) {
				if(i == 0){
					paddingResult[source.length + i] = (byte)0x80;
				}
				else{
					paddingResult[source.length + i] = (byte)0x00;
				}
				//System.out.println("paddingResult[i] :: " +  Integer.toHexString(0xff & paddingResult[source.length+i]));
			}			
		} else {
			paddingResult = source;
		}

		return paddingResult;
	}
	
	public byte[] removePadding(byte[] source, int blockSize) {
		byte[] paddingResult = null;
		boolean isPadding = false;
		int paddingIdx = 0;
		//System.out.println("source len :: " + source.length);
		//System.out.println("source[0] :: " +  Integer.toHexString(0xff & source[source.length-1]));
		if(source[source.length - 1] == (byte)0x00 || source[source.length - 1] == (byte)0x80){
			//padding
			for(int i = source.length-1; i >=0; i-- ){
				//System.out.println("i :: " + i);
				//System.out.println("source[i] :: " +  Integer.toHexString(0xff & source[i]));
				if(source[i] ==  (byte)0x80){
					//System.out.println("idx :: " + i);
					paddingIdx = i;
					break;					
				}
			}
			paddingResult = new byte[paddingIdx];
			System.arraycopy(source, 0, paddingResult, 0, paddingIdx);
			return paddingResult;
			
		}
		else{
			//not padding
			return source;
		}
		//if(lastValue < (blockSize - 1)) {
		//	int zeroPaddingCount = lastValue - 1;
			
		//	for(int i=2;i<(zeroPaddingCount + 2);i++) {
		//		if(source[source.length - i] != PADDING_VALUE) {
		//			isPadding = true;
		//			break;
		//		}
		//	}
			
		//	isPadding = true;
		//} else {
			
		//	isPadding = false;
		//}
		/*
		if(isPadding) {
			paddingResult = new byte[source.length - lastValue];
			System.arraycopy(source, 0, paddingResult, 0, paddingResult.length);
		} else {
			paddingResult = source;
		}		
		
		return paddingResult;
		*/
	}
	
	public String getName() {
		return name;
	}
	
}
