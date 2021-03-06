package org.rstudio.studio.client.workbench.views.source.editors.text.spelling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.rstudio.core.client.CsvReader;
import org.rstudio.core.client.CsvWriter;
import org.rstudio.core.client.ResultCallback;
import org.rstudio.core.client.widget.NullProgressIndicator;
import org.rstudio.studio.client.common.spelling.RealtimeSpellChecker;
import org.rstudio.studio.client.server.Void;
import org.rstudio.studio.client.workbench.views.source.model.DocUpdateSentinel;

import com.google.gwt.event.shared.HandlerRegistration;

public abstract class SpellingContext implements RealtimeSpellChecker.Context
{
   public SpellingContext(DocUpdateSentinel docUpdateSentinel)
   {
      docUpdateSentinel_ = docUpdateSentinel;
      realtimeSpellChecker_ = new RealtimeSpellChecker(this);
   }

   // Legacy checkSpelling function for popup dialog
   public void checkSpelling(SpellingDoc spellingDoc)
   {
      if (isSpellChecking_)
         return;
      isSpellChecking_ = true;
      new CheckSpelling(spellChecker(), spellingDoc,
                        new SpellingDialog(),
                        new InitialProgressDialog(1000),
                        new ResultCallback<Void, Exception>()
                        {
                           @Override
                           public void onSuccess(Void result)
                           {
                              isSpellChecking_ = false;
                           }

                           @Override
                           public void onFailure(Exception e)
                           {
                              isSpellChecking_ = false;
                           }

                           @Override
                           public void onCancelled()
                           {
                              isSpellChecking_ = false;
                           }
                        });
   }

   public void onDismiss()
   {
      while (releaseOnDismiss_.size() > 0)
         releaseOnDismiss_.remove(0).removeHandler();
   }

   @Override
   public ArrayList<String> readDictionary()
   {
      ArrayList<String> ignoredWords = new ArrayList<>();
      String ignored = docUpdateSentinel_.getProperty(IGNORED_WORDS);
      if (ignored != null)
      {
         Iterator<String[]> iterator = new CsvReader(ignored).iterator();
         if (iterator.hasNext())
         {
            String[] words = iterator.next();
            ignoredWords.addAll(Arrays.asList(words));
         }
      }
      return ignoredWords;
   }

   @Override
   public void writeDictionary(ArrayList<String> ignoredWords)
   {
      CsvWriter csvWriter = new CsvWriter();
      for (String ignored : ignoredWords)
         csvWriter.writeValue(ignored);
      csvWriter.endLine();
      docUpdateSentinel_.setProperty(IGNORED_WORDS,
                                     csvWriter.getValue(),
                                     new NullProgressIndicator());
   }

   @Override
   public void releaseOnDismiss(HandlerRegistration handler)
   {
      releaseOnDismiss_.add(handler);
   }

   protected RealtimeSpellChecker spellChecker()
   {
      return realtimeSpellChecker_;
   }

   private boolean isSpellChecking_;

   private final RealtimeSpellChecker realtimeSpellChecker_;

   private final DocUpdateSentinel docUpdateSentinel_;

   private ArrayList<HandlerRegistration> releaseOnDismiss_ = new ArrayList<>();

   private final static String IGNORED_WORDS = "ignored_words";
}