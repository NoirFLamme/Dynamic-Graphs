import java.util.ArrayList;

public class Request {
    int duration;

    public Request(){
        this.duration = 0;
    }
    public String processQueries(ArrayList<Query> queries){
        String result = "";
        for(Query query : queries){
            query.executeQuery();
            this.duration += query.getDuration();
            result += query.getResult();
            result += '\n';
        }
        return result;
    }
}
