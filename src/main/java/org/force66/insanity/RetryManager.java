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

import java.util.concurrent.Callable;

import org.apache.commons.lang3.Validate;

/**
 * Implementation of the Retry pattern.
 * @author D. Ashmore
 * @see RetryAlgorithm
 */
public class RetryManager<T> {
	
	private RetryAlgorithm retryAlgorithm;

	/**
	 * @see DefaultRetryAlgorithm
	 */
	public RetryManager() {
		this(new DefaultRetryAlgorithm());
	}
	
	public RetryManager(RetryAlgorithm algorithm) {
		Validate.notNull(algorithm, "Null algorithm not allowed.");
		retryAlgorithm = algorithm;
		
	}
	
	public T invoke(Callable<T> operation) {
		Validate.notNull(operation, "Null operation not allowed.");
		int nbrTries = 0;
		Exception lastFailure=null;
		retryAlgorithm.reset();

		while (retryAlgorithm.isExecutionAllowed())  {
			nbrTries++;
			try {
				T output = operation.call();			
				return output;
			}
			catch (Exception e) {
				lastFailure=e;
				retryAlgorithm.reportExecutionFailure(e);				
			}
		}
		
		throw new RetryException("invocation not successful.", lastFailure)
		.addContextValue("callable class", operation.getClass().getName())
		.addContextValue("callable", operation.toString())
		.addContextValue("nbrTries", nbrTries);
	}

	protected RetryAlgorithm getRetryAlgorithm() {
		return retryAlgorithm;
	}

}
