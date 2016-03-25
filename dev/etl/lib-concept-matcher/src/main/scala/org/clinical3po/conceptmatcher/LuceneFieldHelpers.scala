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

import org.apache.lucene.document._
import org.apache.lucene.document.Field.Store
import scala.language.implicitConversions

object LuceneText {

  /**
   * Augments a String with a method to convert it to a LuceneText object
   */
  implicit def stringToLuceneTextWrapper(v: String) = new {
    def toLuceneText = new LuceneText(v)
  }

}

/**
 * Simple wrapper to differentiate between simple Strings and text strings
 * in the context of a Lucene index
 */
case class LuceneText(text: String) {
  override def toString: String = text
}

object LuceneFieldHelpers {
  import annotation.implicitNotFound

  /**
   * Base trait for type classes that provides the logic to add a typed object to
   * a Lucene index.
   */
  @implicitNotFound("No member of type class LuceneFieldLike in scope for ${T}")
  trait LuceneFieldLike[@specialized(scala.Int, scala.Long, scala.Float, scala.Double) T] {
    /**
     * Adds an indexed and optionally stored field to the Lucene Document
     *
     * @param doc The Lucene Document
     * @param name The name of the field
     * @param value The value of the field
     * @param stored Whether the value should be stored
     */
    def addIndexedField(doc: Document, name: String, value: T, stored: Store)

    /**
     * Adds a stored only field to the Lucene Document
     *
     * @param doc The Lucene Document
     * @param name The name of the field
     * @param value The value of the field
     */
    def addStoredOnlyField(doc: Document, name: String, value: T)
  }

  object LuceneFieldLike {

    /**
     * LuceneFieldLike implementation for String
     */
    implicit object LuceneFieldLikeString extends LuceneFieldLike[String] {
      def addIndexedField(doc: Document, name: String, value: String, stored: Store) {
        doc.add(new StringField(name, value, stored))
      }

      def addStoredOnlyField(doc: Document, name: String, value: String) {
        doc.add(new StoredField(name, value))
      }
    }

    /**
     * LuceneFieldLike implementation for LuceneText
     */
    implicit object LuceneFieldLikeText extends LuceneFieldLike[LuceneText] {
      def addIndexedField(doc: Document, name: String, value: LuceneText, stored: Store) {
        doc.add(new StringField(name, value.text, stored))
      }

      def addStoredOnlyField(doc: Document, name: String, value: LuceneText) {
        doc.add(new StoredField(name, value.text))
      }
    }

    /**
     * LuceneFieldLike implementation for Int
     */
    implicit object LuceneFieldLikeInt extends LuceneFieldLike[Int] {
      def addIndexedField(doc: Document, name: String, value: Int, stored: Store) {
        doc.add(new IntField(name, value, stored))
      }

      def addStoredOnlyField(doc: Document, name: String, value: Int) {
        doc.add(new StoredField(name, value))
      }
    }

    /**
     * LuceneFieldLike implementation for Long
     */
    implicit object LuceneFieldLikeLong extends LuceneFieldLike[Long] {
      def addIndexedField(doc: Document, name: String, value: Long, stored: Store) {
        doc.add(new LongField(name, value, stored))
      }

      def addStoredOnlyField(doc: Document, name: String, value: Long) {
        doc.add(new StoredField(name, value))
      }
    }

    /**
     * LuceneFieldLike implementation for Float
     */
    implicit object LuceneFieldLikeFloat extends LuceneFieldLike[Float] {
      def addIndexedField(doc: Document, name: String, value: Float, stored: Store) {
        doc.add(new FloatField(name, value, stored))
      }

      def addStoredOnlyField(doc: Document, name: String, value: Float) {
        doc.add(new StoredField(name, value))
      }
    }

    /**
     * LuceneFieldLike implementation for Double
     */
    implicit object LuceneFieldLikeDouble extends LuceneFieldLike[Double] {
      def addIndexedField(doc: Document, name: String, value: Double, stored: Store) {
        doc.add(new DoubleField(name, value, stored))
      }

      def addStoredOnlyField(doc: Document, name: String, value: Double) {
        doc.add(new StoredField(name, value))
      }
    }

  }

  /**
   * Augments a Lucene Document with methods to make it easier to add new fields
   */
  implicit def documentWrapper(doc: Document) = new {

    /**
     * Adds a new field that gets indexed and stored
     *
     * @param name The name of the field
     * @param value The value of the field
     * @return The Lucene Document itself
     */
    def addIndexedStoredField[T: LuceneFieldLike](name: String, value: T): Document = {
      implicitly[LuceneFieldLike[T]].addIndexedField(doc, name, value, Store.YES)
      doc
    }

    /**
     * Adds a new field that gets indexed and stored
     *
     * @param name The name of the field
     * @param valueOpt The value of the field
     * @return The Lucene Document itself
     */
    def addIndexedStoredField[T: LuceneFieldLike](name: String, valueOpt: Option[T]): Document = {
      valueOpt.foreach { value => implicitly[LuceneFieldLike[T]].addIndexedField(doc, name, value, Store.YES) }
      doc
    }

    /**
     * Adds a new field that gets stored but not indexed
     *
     * @param name The name of the field
     * @param value The value of the field
     * @return The Lucene Document itself
     */
    def addStoredOnlyField[T: LuceneFieldLike](name: String, value: T): Document = {
      implicitly[LuceneFieldLike[T]].addStoredOnlyField(doc, name, value)
      doc
    }

    /**
     * Adds a new field that gets stored but not indexed
     *
     * @param name The name of the field
     * @param valueOpt The value of the field
     * @return The Lucene Document itself
     */
    def addStoredOnlyField[T: LuceneFieldLike](name: String, valueOpt: Option[T]): Document = {
      valueOpt.foreach { value => implicitly[LuceneFieldLike[T]].addStoredOnlyField(doc, name, value) }
      doc
    }

    /**
     * Adds a new field that gets indexed but not stored
     *
     * @param name The name of the field
     * @param value The value of the field
     * @return The Lucene Document itself
     */
    def addIndexedOnlyField[T: LuceneFieldLike](name: String, value: T): Document = {
      implicitly[LuceneFieldLike[T]].addIndexedField(doc, name, value, Store.NO)
      doc
    }

    /**
     * Adds a new field that gets indexed but not stored
     *
     * @param name The name of the field
     * @param valueOpt The value of the field
     * @return The Lucene Document itself
     */
    def addIndexedOnlyField[T: LuceneFieldLike](name: String, valueOpt: Option[T]): Document = {
      valueOpt.foreach { value => implicitly[LuceneFieldLike[T]].addIndexedField(doc, name, value, Store.NO) }
      doc
    }

  }

}
