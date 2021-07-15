package ma.emsi.pfa.web;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.Comment;
import com.restfb.types.Post;
import ma.emsi.pfa.dao.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;
import java.util.List;

@Controller
public class CompteController {

    @Autowired
    CompteRepository compteRepository;

    private String token = "Your-Api-Key-here";
    private FacebookClient facebookClient = new DefaultFacebookClient(token, Version.LATEST);


    @GetMapping("/publications")
    public String publications(Model model, Long id) {
        /*Compte compte = compteRepository.findById(id).get();
        String page = compte.getUrl().split("/")[compte.getUrl().split("/").length-1];
        Connection<Post> feed = facebookClient.fetchConnection(page+"/feed", Post.class);*/
        Connection<Post> feed = facebookClient.fetchConnection("me/feed", Post.class);
        List<Post> posts = feed.getData();
        model.addAttribute("posts", posts);
        return "publications";
    }


    @GetMapping("/comments")
    public String comments(@RequestParam(name = "id") String id,
                           @RequestParam(name = "date") String date,
                           @RequestParam(name = "message") String message,
                           Model model) throws Exception {
        Connection<Comment> comment = facebookClient.fetchConnection(id + "/comments", Comment.class);
        List<Comment> comments = comment.getData();
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Iterator<Comment> commentIterator = comments.iterator();
            if (!commentIterator.hasNext())
                return "null";
            float totalScore = 0, totalMagnitude = 0;
            int totalPos = 0, totalNeg = 0, i = 0, totalNeu = 0;
            while (commentIterator.hasNext()) {
                String text = commentIterator.next().getMessage();
                Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
                Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
                totalScore += sentiment.getScore();
                totalMagnitude += sentiment.getMagnitude();
                if (sentiment.getScore() > 0.3)
                    totalPos += 1;
                else if (sentiment.getScore() < 0)
                    totalNeg += 1;
                else
                    totalNeu += 1;
                i++;
            }
            String sentiment;
            if (totalScore < 0)
                sentiment = "negatifs";
            else if (totalScore > 0 && totalScore <= 0.3)
                sentiment = "neutres";
            else
                sentiment = "positifs";
            model.addAttribute("resultatScore", Math.round(((totalScore / i) * 100) * 100) / 100);
            model.addAttribute("totalPos", totalPos);
            model.addAttribute("id", id);
            model.addAttribute("total", totalPos + totalNeg + totalNeu);
            model.addAttribute("totalNeg", totalNeg);
            model.addAttribute("totalNeu", totalNeu);
            model.addAttribute("comment", comment);
            model.addAttribute("sentiment", sentiment);
            model.addAttribute("date", date);
            model.addAttribute("message", message);
            model.addAttribute("resultatMagnitude", Math.round(((totalMagnitude / i) * 100) * 100) / 100);
        }
        return "comments";
    }
}
