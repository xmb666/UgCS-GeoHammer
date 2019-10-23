package com.github.thecoldwine.sigrun.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.github.thecoldwine.sigrun.common.ext.Block;
import com.github.thecoldwine.sigrun.common.ext.SgyFile;
import com.github.thecoldwine.sigrun.serialization.BinaryHeaderFormat;
import com.github.thecoldwine.sigrun.serialization.BinaryHeaderReader;
import com.github.thecoldwine.sigrun.serialization.TextHeaderReader;
import com.github.thecoldwine.sigrun.serialization.TraceHeaderFormat;
import com.github.thecoldwine.sigrun.serialization.TraceHeaderReader;
import com.ugcs.gprvisualizer.gpr.SgyLoader;

public class Repack {


	private final Charset charset = Charset.forName("UTF8");
    private final BinaryHeaderFormat binaryHeaderFormat = SgyLoader.makeBinHeaderFormat();
    private final TraceHeaderFormat traceHeaderFormat = SgyLoader.makeTraceHeaderFormat();

    private final TextHeaderReader textHeaderReader = new TextHeaderReader(charset);
    private final BinaryHeaderReader binaryHeaderReader = new BinaryHeaderReader(binaryHeaderFormat);
    private final TraceHeaderReader traceHeaderReader = new TraceHeaderReader(traceHeaderFormat);

	
	public static void main(String[] args) throws Exception {
		new Repack().example();
	}
	
	public void example() throws Exception{

		
		
		
		//SgyFile_______ 
		//   |			|
		//Channel <- Block <- Trace <- 	Scan <- LocalScan
		//           file pointer
		//                    header, data
		//								normvalues, lat, lon
		//										scr x, scr y
		
		FileChannel chan = new FileInputStream("d:\\georadarData\\Greenland\\2018-07-04-15-27-03-gpr-shift.sgy").getChannel();
		
		Block txtHdrBlock = new Block(chan, 0, TextHeader.TEXT_HEADER_SIZE);
		Block binHdrBlock = new Block(txtHdrBlock, BinaryHeader.BIN_HEADER_LENGTH);
		
		BinaryHeader binaryHeader = binaryHeaderReader.read(binHdrBlock.read().array());
		
		Block lastBlock = binHdrBlock; 
		
		List<Block> blocks = new ArrayList<>();
		//blocks.add(txtHdrBlock);
		//blocks.add(binHdrBlock);
		
		try {
			do {
				Block traceHdrBlock = new Block(lastBlock, TraceHeader.TRACE_HEADER_LENGTH);
		        TraceHeader header = traceHeaderReader.read(traceHdrBlock.read().array());
		        int dataLength = binaryHeader.getDataSampleCode().getSize() * header.getNumberOfSamples();
				
		        Block traceDataBlock = new Block(traceHdrBlock, dataLength);
		        
		        blocks.add(traceHdrBlock);
		        blocks.add(traceDataBlock);
		        
		        lastBlock = traceDataBlock;
		        System.out.println("read");
			}while(lastBlock != null);
		}catch(IOException e) {
			System.out.println("exception finish");
		}
		System.out.println("finish " + new String(txtHdrBlock.read().array()));
        
		
		
		FileOutputStream fos = new FileOutputStream("d:\\georadarData\\created.sgy");
		FileChannel writechan = fos.getChannel();
		
		writechan.write(ByteBuffer.wrap(txtHdrBlock.read().array()));
		writechan.write(ByteBuffer.wrap(binHdrBlock.read().array()));
		
		for(int i = 540; i<blocks.size()-550; i++) {
			Block block = blocks.get(i);
			writechan.write(ByteBuffer.wrap(block.read().array()));
			//System.out.println("write " + bb.array().length + "   " + i);
			
		}
		
		//writechan.write(ByteBuffer.wrap("sdpadfj asdf laisdhflaishdfklahsdilf".getBytes()));
		
		writechan.close();
		fos.close();
		
		//SgyFile file = Loader.load(""); 
		
		//List<Trace> traces = file.loadTraces();
		
		
		////
		
		//SgyFile file2 = file.makeClone("new.sgy");
		
		//file2.addTraces(traces);
		
		//file2.close();
		
	}
	
	
	
	
}