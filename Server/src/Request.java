import java.util.ArrayList;

public class Request {
    int duration;
    int id;

    public Request(int id){
        this.duration = 0;
        this.id = id;
    }
    public String processQueries(ArrayList<Query> queries){
        String result = "";
        for(Query query : queries){
            query.executeQuery(this.id);
            this.duration += query.getDuration();
            result += query.getResult();
            result += '\n';
        }
        return result;
    }
}
