/*
 * This software is licensed under the Apache License, Version 2.0
 * (the "License") agreement; you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.force66.insanity;

/**
 * Manages retry logic for a RetryManager.
 * @author D. Ashmore
 *
 */
public interface RetryAlgorithm {
	
	/**
	 * Implementors report if operation execution should proceed.  If a time
	 * period should elapse before the next execution, Implementors
	 * should place the wait here.
	 */
	public boolean isExecutionAllowed();
	
	/**
	 * Will be invoked when an execution failure is encountered.
	 * @param exceptionThrown
	 */
	public void reportExecutionFailure(Exception exceptionThrown);
	
	/**
	 * Implementors re-initialize for a new invocation.
	 */
	public void reset();

}
