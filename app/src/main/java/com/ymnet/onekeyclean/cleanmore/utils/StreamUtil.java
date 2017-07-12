package com.ymnet.onekeyclean.cleanmore.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Janan
 * @data 11/19/15 12:07.
 * @since version  3.4
 */
public class StreamUtil {

    public static void closeInputStream(InputStream...inputStreams){

        if(inputStreams!=null && inputStreams.length>0){

            for(InputStream stream:inputStreams){
                if(stream!=null){
                    try{
                        stream.close();
                    }catch (Exception e){

                    }
                }
            }

        }
    }


    public static void closeOutputStream(OutputStream...outputStreams){
        if(outputStreams!=null && outputStreams.length>0){
            for(OutputStream stream:outputStreams){
                if(stream!=null){
                    try{
                        stream.close();
                    }catch (Exception e){

                    }
                }
            }

        }
    }

    public static void closeReader(Reader... readers){
        if(readers!=null && readers.length>0){
            for(Reader reader : readers){
                if(reader!=null){
                    try{
                        reader.close();
                    }catch (Exception e){

                    }
                }
            }
        }
    }

    public static void closeWriter(Writer... writers){
        if(writers!=null && writers.length>0){
            for(Writer writer : writers){
                if(writer!=null){
                    try{
                        writer.close();
                    }catch (Exception e){

                    }
                }
            }
        }
    }
}
