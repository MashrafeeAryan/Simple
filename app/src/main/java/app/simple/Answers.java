package app.simple;
import android.app.Activity;import android.widget.*;import org.json.*;import java.util.*;
final class Answers {
 final Activity a;final LinearLayout container;final List<EditText[]> rows=new ArrayList<>();
 Answers(Activity activity,LinearLayout parent,JSONObject saved){a=activity;container=new LinearLayout(a);container.setOrientation(LinearLayout.VERTICAL);parent.addView(container);if(saved!=null){Iterator<String> k=saved.keys();while(k.hasNext()){String q=k.next();add(q,saved.optString(q));}}Button add=new Button(a);add.setText("Add saved question and answer");parent.addView(add);add.setOnClickListener(v->add("",""));}
 void add(String q,String value){LinearLayout row=new LinearLayout(a);row.setOrientation(LinearLayout.VERTICAL);EditText question=new EditText(a),answer=new EditText(a);question.setHint("Exact question");question.setText(q);answer.setHint("Your saved answer");answer.setText(value);row.addView(question);row.addView(answer);Button remove=new Button(a);remove.setText("Remove answer");row.addView(remove);EditText[] pair={question,answer};rows.add(pair);container.addView(row);remove.setOnClickListener(v->{rows.remove(pair);container.removeView(row);});}
 JSONObject read()throws JSONException{JSONObject out=new JSONObject();for(EditText[] pair:rows){String q=pair[0].getText().toString().toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+"," ").trim();if(!q.isEmpty())out.put(q,pair[1].getText().toString());}return out;}
}
