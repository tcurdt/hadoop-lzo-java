package org.vafer.lzo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;

import org.apache.commons.io.input.CountingInputStream;

public final class LzoIndexer {
	
	private final static long F_ADLER32_D     = 0x00000001L;
	private final static long F_ADLER32_C     = 0x00000002L;
	private final static long F_CRC32_D       = 0x00000100L;
	private final static long F_CRC32_C       = 0x00000200L;
	private final static long F_H_FILTER      = 0x00000800L;
	private final static long F_H_EXTRA_FIELD = 0x00000040L;

    private final static byte magic[] = { (byte)0x89, 0x4C, 0x5A, 0x4f, 0x00, 0x0D, 0x0A, 0x1A, 0x0A };

	public void createIndex(InputStream fis, OutputStream fos) throws IOException, DataFormatException {
		try {
			
			CountingInputStream cis = new CountingInputStream(fis);		
			DataInputStream is = new DataInputStream(cis);
			DataOutputStream os = new DataOutputStream(fos);
			
	        byte m[] = new byte[magic.length];

	        if (is.read(m) != m.length) {
	            throw new DataFormatException("failed to read header");        	
	        }
	        
	        for (int i = 0; i < m.length; i++) {
				if (magic[i] != m[i]) {
		            throw new DataFormatException("not a lzo file " + i);				
				}
			}

	        int version = is.readShort(); // version
	        
	        if (version < 0x0900) {
	            throw new DataFormatException("cannot read version " + version);        	
	        }
	        
	        is.readShort(); // lib version
	        
	        if (version >= 0x0940) { 
	            is.readShort(); // extract version        	
	        }

	        is.readByte(); // method
	        if (version >= 0x0940) {
	            is.readByte(); // level        	
	        }

	        int flags = is.readInt();
	        if ((flags & F_H_FILTER) != 0) {
	        	is.readInt(); // filter
	        }

	        int numCompressedChecksums = 0;
	        if ((flags & F_ADLER32_C) != 0) numCompressedChecksums++;
	        if ((flags & F_CRC32_C) != 0) numCompressedChecksums++;
	        
	        int numDecompressedChecksums = 0;
	        if ((flags & F_ADLER32_D) != 0) numDecompressedChecksums++;
	        if ((flags & F_CRC32_D) != 0) numDecompressedChecksums++;
	                
	        is.readInt(); // mode
	        is.readInt(); // mtime_low
	        if (version >= 0x0940) {
	            is.readInt(); // mtime_high        	
	        }
	        
	        int len = is.readUnsignedByte(); // name len
	        
	        char[] name = new char[len];
	        for(int i=0; i<len; i++) {
	        	name[i] = (char)is.readByte();
	        }
	        
	        is.readInt(); // checksum

	        if ((flags & F_H_EXTRA_FIELD) != 0) {
	        	int extra_len = is.readInt();
	            for(int i=0; i<extra_len; i++) {
	            	is.readByte();
	            }
	            is.readInt(); // checksum
	        }
	        
	        while(true) {
	        	long pos = cis.getByteCount();
	        	
	            long uncompressed_blocksize = is.readInt();
	            if (uncompressed_blocksize == 0) {
	            	break;
	            }
	            long compressed_blocksize = is.readInt();
	            
	            os.writeLong(pos);
	            
	    		int numChecksumsToSkip = (uncompressed_blocksize == compressed_blocksize) ?
	    				numDecompressedChecksums : numDecompressedChecksums + numCompressedChecksums;
	                        
	            long skip = compressed_blocksize + (4 * numChecksumsToSkip);
	            while(skip > 0) {
	            	long s = is.skip(skip);
	            	if (s > 0) {
	            		skip -= s;
	            	}
	            }
	        }		
			
		} finally {
			fis.close();
			fos.close();
		}
	}
}
