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

package org.clinical3po.conceptmatcher

import java.nio.file.Path
import java.nio.file.Paths
import org.apache.lucene.store.{MMapDirectory, SimpleFSDirectory, Directory}
import javax.annotation.Nonnull
import com.google.common.base.Preconditions

/**
 * Base trait that creates a Lucene filesystem Directory from a root path
 */
trait FSLuceneDirectoryCreator {
  protected def directoryConstructor: Path => Directory

  @Nonnull
  def luceneDirectoryFromPath(@Nonnull path: Path): Directory = {
    Preconditions.checkNotNull(path)
    directoryConstructor(path)
  }

}

/**
 * An FSLuceneDirectoryCreator that creates a SimpleFSDirectory
 */
trait SimpleFSLuceneDirectoryCreator extends FSLuceneDirectoryCreator {
  override protected def directoryConstructor = new SimpleFSDirectory(_)
}

/**
 * An FSLuceneDirectoryCreator that creates an MMapDirectory
 */
trait MMapFSLuceneDirectoryCreator extends FSLuceneDirectoryCreator {
  override protected def directoryConstructor = new MMapDirectory(_)
}
