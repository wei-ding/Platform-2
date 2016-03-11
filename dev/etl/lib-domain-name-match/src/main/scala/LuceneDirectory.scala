/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.clinical3po.namematcher

import java.io.File
import org.apache.lucene.store.{MMapDirectory, SimpleFSDirectory, RAMDirectory, Directory}
import org.apache.lucene.index.{IndexNotFoundException, DirectoryReader}
import org.apache.lucene.search.IndexSearcher
import javax.annotation.Nonnull

/**
 * Base trait for configuring the Directory of a Lucene index
 */
trait LuceneDirectory {
  /**
   * The Lucene Directory object, must be defined by one of the concrete classes
   */
  protected def directory: Directory

  /**
   * Calls the provided function with an instance of an Option[DirectoryReader] that
   * gets created from the current Lucene Directory object.
   * When the parameter is None, it means that the index has not
   * been created yet, or it's corrupted.
   */
  def withDirectoryReader[T](@Nonnull f: Option[DirectoryReader] => T): T =
    try {
      val indexReader = DirectoryReader.open(directory)
      try {
        f(Some(indexReader))
      } finally {
        indexReader.close()
      }
    } catch {
      case e: IndexNotFoundException =>
        f(None)
    }

  /**
   * Calls the provided function with an instance of an Option[IndexSearcher] that
   * gets created from the current Lucene Directory object.
   * When the parameter is None, it means that the index has not
   * been created yet, or it's corrupted.
   */
  def withIndexSearcher[T](@Nonnull f: Option[IndexSearcher] => T): T =
    withDirectoryReader { indexReaderOpt =>
      f(indexReaderOpt.map(new IndexSearcher(_)))
    }

}

/**
 * A LuceneDirectory that uses a RAM based directory to store the index
 */
trait RamLuceneDirectory extends LuceneDirectory {
  /**
   * The instance of a RAM based Directory
   */
  protected lazy val directory = new RAMDirectory()
}

/**
 * A LuceneDirectory that based on filesystem
 */
trait FSLuceneDirectory extends LuceneDirectory {
  self: LuceneIndexPathProvider with FSLuceneDirectoryCreator =>
  protected lazy val directory = withIndexPath(luceneDirectoryFromPath)
}

/**
 * A LuceneDirectory that uses a SimpleFSDirectory based on the ServiceRootLucenePathProvider
 */
trait DefaultFSLuceneDirectory
  extends FSLuceneDirectory
  with ServiceRootLucenePathProvider
  with SimpleFSLuceneDirectoryCreator
