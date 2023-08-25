package Server;

import db.bean.Peer;
import shared.LeafNodeService;
import utils.Constants;
import utils.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * @description: An implementation of services offered by leaf node.
 * @author: Xue Zhang
 * @date: 2022-03-18
 * @version: 1.0
 */
public class LeafNodeServiceImpl implements LeafNodeService {

    private byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    @Override
    public Response obtain(Peer peer) {
        // to read a file by using stream
        FileInputStream fileInputStream = null;
        byte[] buffer = new byte[Constants.BUFFER_SIZE];
        // the last 2 nodes used to store blockNum & tailLen
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        Response response = null;
        int numberOfBytes;
        try {
            fileInputStream = new FileInputStream(peer.getFilepath());
            numberOfBytes = fileInputStream.read(buffer);
            int tailLen = 0, blockNum = 0;

            while (numberOfBytes != -1) {
                if (numberOfBytes == Constants.BUFFER_SIZE) {
                    blockNum += 1;
                } else {
                    tailLen = numberOfBytes;
                }
                data.add(buffer);
                numberOfBytes = fileInputStream.read(buffer);
            }
            // to store blockNum & tailLen for creating a new file
            data.add(intToBytes(blockNum));
            data.add(intToBytes(tailLen));

            response = new Response(Constants.RETRIEVE_CODE_SUC, "Read all data of the file.", data);
        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(Constants.RETRIEVE_CODE_ERR_READ, "IO exception at reading step");
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                response = new Response(Constants.RETRIEVE_CODE_ERR_CLOSE, "IO exception at close step");
            } finally {
                return response;
            }
        }
    }

    @Override
    public Response invalidNotice() {
        return null;
    }

}
