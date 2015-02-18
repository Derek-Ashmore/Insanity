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

import org.apache.commons.lang3.Validate;

/**
 * Default RetryAlgorithm implementation.  This implementation will wait a configurable amount
 * of time between each retry with a maximum number of retries allowed.;
 * @author D. Ashmore
 *
 */
public class DefaultRetryAlgorithm implements RetryAlgorithm {
	
	public static final Integer DEFAULT_MAX_NBR_RETRIES=5;
	public static final Long 	DEFAULT_TIME_BETWEEN_RETRIES_IN_MILLIS=5L*60000L;
	
	private Integer maxNbrRetries;
	private Long	timeBetweenRetriesInMillis;
	
	private int nbrFailures = 0;
	private long timeLastFailureInMillis=0;

	public DefaultRetryAlgorithm() {
		this(DEFAULT_MAX_NBR_RETRIES, DEFAULT_TIME_BETWEEN_RETRIES_IN_MILLIS);
	}
	
	public DefaultRetryAlgorithm(Integer maxNbrRetries, Long timeBetweenRetriesInMillis) {
		Validate.notNull(maxNbrRetries, "Null maxNbrRetries not allowed.");
		Validate.notNull(timeBetweenRetriesInMillis, "Null timeBetweenRetriesInMillis not allowed.");
		Validate.isTrue(maxNbrRetries>0, "maxNbrRetries must be larger than 0.  value=%s", maxNbrRetries);
		Validate.isTrue(timeBetweenRetriesInMillis>0L, "timeBetweenRetriesInMillis must be larger than 0.  value=%s", timeBetweenRetriesInMillis);
		
		this.maxNbrRetries = maxNbrRetries;
		this.timeBetweenRetriesInMillis = timeBetweenRetriesInMillis;

	}

	public boolean isExecutionAllowed() {
		if (nbrFailures >= maxNbrRetries)  {
			return false;
		}
		if (timeLastFailureInMillis==0) {
			return true;
		}

		long millisUntilNextAttempt= timeBetweenRetriesInMillis - 
				(System.currentTimeMillis() - timeLastFailureInMillis);
		
		if (millisUntilNextAttempt < 0) {
			return true;
		}
		
		try {
			Thread.sleep(millisUntilNextAttempt);
		} catch (InterruptedException e) {
			throw new RetryException(e);
		}

		return true;
	}

	public void reportExecutionFailure(Exception exceptionThrown) {
		nbrFailures++;
		timeLastFailureInMillis=System.currentTimeMillis();
	}

	public Integer getMaxNbrRetries() {
		return maxNbrRetries;
	}

	public Long getTimeBetweenRetriesInMillis() {
		return timeBetweenRetriesInMillis;
	}

	public int getNbrFailures() {
		return nbrFailures;
	}

	public long getTimeLastFailureInMillis() {
		return timeLastFailureInMillis;
	}

	public void reset() {
		nbrFailures = 0;
		timeLastFailureInMillis=0;
	}

}
