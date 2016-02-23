// THIS SOURCE CODE IS SUPPLIED "AS IS" WITHOUT WARRANTY OF ANY KIND, AND ITS AUTHOR AND THE JOURNAL OF MACHINE LEARNING RESEARCH (JMLR) AND JMLR'S PUBLISHERS AND DISTRIBUTORS, DISCLAIM ANY AND ALL WARRANTIES, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND ANY WARRANTIES OR NON INFRINGEMENT. THE USER ASSUMES ALL LIABILITY AND RESPONSIBILITY FOR USE OF THIS SOURCE CODE, AND NEITHER THE AUTHOR NOR JMLR, NOR JMLR'S PUBLISHERS AND DISTRIBUTORS, WILL BE LIABLE FOR DAMAGES OF ANY KIND RESULTING FROM ITS USE. Without lim- iting the generality of the foregoing, neither the author, nor JMLR, nor JMLR's publishers and distributors, warrant that the Source Code will be error-free, will operate without interruption, or will meet the needs of the user.
// 
// --------------------------------------------------------------------------
// 
// Copyright 2016 Stephen Piccolo
// 
// This file is part of ML-Flex.
// 
// ML-Flex is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// any later version.
// 
// ML-Flex is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with ML-Flex. If not, see <http://www.gnu.org/licenses/>.

//package mlflex.transformation;
//
//import mlflex.core.Settings;
//import mlflex.helper.MiscUtilities;
//
///** After raw data values are retrieved from the raw data sources, classes that inherit from this class can be used to transform those values.
// * @author Stephen Piccolo
// */
//public abstract class AbstractTransformer
//{
//    /** This abstract method is designed to be overridden by classes that inherit from this class. These overriding methods do the work of transforming the raw values.
//     * @param value Value to be transformed
//     * @return Transformed value
//     * @throws Exception
//     */
//    protected abstract String Transform(String value) throws Exception;
//
//    /** This method is a convenience method that uses guard clause(s) before the value is transformed.
//     * @param value Value to be transformed
//     * @return Transformed value
//     * @throws Exception
//     */
//    public String TransformValue(String value) throws Exception
//    {
//        if (MiscUtilities.IsMissing(value))
//            return Settings.MISSING_VALUE_STRING;
//
//        return Transform(value);
//    }
//}
