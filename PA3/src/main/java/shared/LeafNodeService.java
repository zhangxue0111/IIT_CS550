package shared;


import db.bean.Peer;
import utils.Response;

/**
 * @description: A set of services offered by leaf nodes servers.
 * @author: Xue Zhang
 * @date: 2022-03-18
 * @version: 1.0
 */
public interface LeafNodeService{

    Response obtain(Peer peer);

    Response invalidNotice();
}