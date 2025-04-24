/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2324.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author marco
 */
public class TestSearch1 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws ParseException 
     */
    public static void main(String[] args) throws IOException, ParseException {

		FSDirectory fsdir = FSDirectory.open(new File("./resources/alice").toPath());
    	//FSDirectory fsdir = FSDirectory.open(new File("./resources/helloworld").toPath());
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(fsdir));

		// Crea un IndexReader
		IndexReader reader = DirectoryReader.open(fsdir);
		int numDocs = reader.numDocs();
		System.out.println("Numero di documenti nell'indice: " + numDocs);

        //Single term query
        //Query q = new TermQuery(new Term("chapter", "rabbit"));
		//Query q = new TermQuery(new Term("chapter_text", "alice"));

        //Boolean query
        BooleanQuery.Builder qb = new BooleanQuery.Builder();
        qb.add(new TermQuery(new Term("chapter", "alice")), BooleanClause.Occur.SHOULD);
        qb.add(new TermQuery(new Term("chapter_text", "alice")), BooleanClause.Occur.SHOULD);
        Query q = qb.build();
        
        
        TopDocs topdocs = searcher.search(q, 10);
        System.out.println("Found " + topdocs.totalHits.value + " document(s). Method 1"); // stampa il numero di risultati
        
        //prendo gli n documenti (con score piu alto) chiesti all'interrogazione e li salvo in un vettore di ScoreDoc
		ScoreDoc[] hits = topdocs.scoreDocs;  

		// scorro gli elementi del vettore e stampo i documenti (ordinati per rilevanza)
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = searcher.doc(hits[i].doc);
			System.out.println(hitDoc.get("chapter") + " \n " + hitDoc.get("chapter_text") + " " +  hits[i].score);
		}
		
		
		//Use of Lucene query Syntax
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Query query = new QueryParser("name", analyzer).parse("chapter:alice OR chapter_text:alice");

		/**
		 * Nel query parser le query possono essere passate sotto forma di stringa, il formato è diverso 
		 * ma il significato e risultato è uguale, rappresenta la query come espressione booleana.
		 * Con questo metodo non bisogna costruire l'interrogazione termine per termine 
		 * ma mettiamo direttamente tutto insieme nella stringa
		 */
		
		topdocs = searcher.search(query, 10);
        System.out.println("\nFound " + topdocs.totalHits.value + " document(s). Method 2");
        
        hits = topdocs.scoreDocs;
		
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = searcher.doc(hits[i].doc);
			System.out.println(hitDoc.get("chapter") + " \n " + hitDoc.get("chapter_text") + " " +  hits[i].score);
		}

		//Multifield Query
	    //add all possible fileds in multifieldqueryparser using indexreader getFieldNames method

		// permette di fare query con più termini, passiamo tutti i campi su cui vogliamo effetuare la ricerca del valore
		QueryParser parser = new MultiFieldQueryParser( new String[] {"author", "chapter", "chapter_text"}, analyzer);
		query = parser.parse("alice");
		
		topdocs = searcher.search(query, 10);
        System.out.println("\nFound " + topdocs.totalHits.value + " document(s). Method ");
        
		hits = topdocs.scoreDocs;
		
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = searcher.doc(hits[i].doc);
			System.out.println(hitDoc.get("chapter") + " \n " + hitDoc.get("chapter_text") + " " +  hits[i].score);
		}

		
        
    }

}
