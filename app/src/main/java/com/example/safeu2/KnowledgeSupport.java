package com.example.safeu2;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safeu2.models.Article;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeSupport extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_support);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        articles = new ArrayList<>();
        loadArticles();

        adapter = new ArticleAdapter(articles, new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Article article) {
                new AlertDialog.Builder(KnowledgeSupport.this)
                        .setTitle(article.getTitle())
                        .setMessage(article.getContent())
                        .setPositiveButton("Close", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadArticles() {
        articles.add(new Article(
                "How to Spot Phishing Links",
                "Learn the telltale signs of a malicious URL before you click.",
                "Phishing is a type of social engineering attack often used to steal user data, including login credentials and credit card numbers. It occurs when an attacker, masquerading as a trusted entity, dupes a victim into opening an email, instant message, or text message.\n\nTips to spot phishing:\n1. Check the sender's email address.\n2. Look for generic greetings.\n3. Beware of urgent or threatening language.\n4. Check the link's actual destination by hovering over it.\n5. Look for poor spelling or grammar."
        ));

        articles.add(new Article(
                "Creating Strong Passwords",
                "Why password complexity matters and how to manage them.",
                "A strong password is your first line of defense against cybercriminals. It's crucial to use different passwords for every account you own. \n\nTips for strong passwords:\n1. Use a mix of uppercase and lowercase letters.\n2. Include numbers and special characters.\n3. Make it at least 12 characters long.\n4. Avoid dictionary words or easily guessable information (like your birthday or pet's name).\n5. Use our built-in Password Generator and secure Vault!"
        ));

        articles.add(new Article(
                "What to Do in an Emergency",
                "How to effectively use the SOS feature when you are in danger.",
                "If you find yourself in an unsafe situation, our Emergency SOS feature is designed to get you help quickly. \n\n1. Ensure your GPS/Location is enabled.\n2. Pre-configure your trusted emergency contacts in the SOS tab.\n3. When in danger, tap the large SOS button.\n4. The app will automatically determine your location and send an SMS with a Google Maps link to your trusted contacts.\n5. Try to get to a safe public space if possible while help arrives."
        ));

        articles.add(new Article(
                "Understanding App Permissions",
                "Why Citizen Safety App needs location and SMS permissions.",
                "To provide you with the best security features, our app requires certain permissions:\n\n1. Location: Needed exclusively for the Emergency SOS feature so we can send your precise coordinates to your trusted contacts when you are in danger.\n2. SMS: Needed to automatically send the distress message to your contacts without requiring you to open your messaging app manually during an emergency.\n\nWe value your privacy. This data is only accessed locally on your device when you trigger an action."
        ));
    }
}
