/**
 * boilerpipe
 *
 * Copyright (c) 2009, 2014 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kohlschutter.boilerpipe.extractors;

import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextDocument;
import com.kohlschutter.boilerpipe.filters.english.IgnoreBlocksAfterContentFilter;
import com.kohlschutter.boilerpipe.filters.english.NumWordsRulesClassifier;
import com.kohlschutter.boilerpipe.filters.english.TerminatingBlocksFinder;
import com.kohlschutter.boilerpipe.filters.heuristics.BlockProximityFusion;
import com.kohlschutter.boilerpipe.filters.heuristics.DocumentTitleMatchClassifier;
import com.kohlschutter.boilerpipe.filters.heuristics.ExpandTitleToContentFilter;
import com.kohlschutter.boilerpipe.filters.heuristics.KeepLargestBlockFilter;
import com.kohlschutter.boilerpipe.filters.heuristics.LargeBlockSameTagLevelToContentFilter;
import com.kohlschutter.boilerpipe.filters.heuristics.ListAtEndFilter;
import com.kohlschutter.boilerpipe.filters.heuristics.TrailingHeadlineToBoilerplateFilter;
import com.kohlschutter.boilerpipe.filters.simple.BoilerplateBlockFilter;
import com.kohlschutter.boilerpipe.labels.DefaultLabels;

/**
 * A full-text extractor which is tuned towards news articles. In this scenario it achieves higher
 * accuracy than {@link DefaultExtractor}.
 */
public final class ArticleExtractor extends ExtractorBase {
  public static final ArticleExtractor INSTANCE = new ArticleExtractor();

  /**
   * Returns the singleton instance for {@link ArticleExtractor}.
   */
  public static ArticleExtractor getInstance() {
    return INSTANCE;
  }

  public boolean process(TextDocument doc) throws BoilerpipeProcessingException {
    return

    new TerminatingBlocksFinder().process(doc)
        | new DocumentTitleMatchClassifier(doc.getTitle()).process(doc)
        | new NumWordsRulesClassifier().process(doc)
        | new IgnoreBlocksAfterContentFilter(60).process(doc)
        | new TrailingHeadlineToBoilerplateFilter().process(doc)
        | new BlockProximityFusion(1, false,false).process(doc)
        | new BoilerplateBlockFilter(DefaultLabels.TITLE).process(doc)
        | new BlockProximityFusion(1, true, true).process(doc)
        | new KeepLargestBlockFilter(true, 150).process(doc)
        | new ExpandTitleToContentFilter().process(doc)
        | new LargeBlockSameTagLevelToContentFilter().process(doc)
        | new ListAtEndFilter().process(doc);
  }
}
