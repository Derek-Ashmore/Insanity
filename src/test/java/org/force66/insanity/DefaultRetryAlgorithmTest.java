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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultRetryAlgorithmTest {
	
	DefaultRetryAlgorithm algorithm;

	@Before
	public void setUp() throws Exception {
		algorithm = new DefaultRetryAlgorithm();
	}

	@Test
	public void testConstructors() throws Exception {
		Assert.assertTrue(algorithm.getMaxNbrRetries() != null);
		Assert.assertTrue(algorithm.getMaxNbrRetries().equals(DefaultRetryAlgorithm.DEFAULT_MAX_NBR_RETRIES));
		Assert.assertTrue(algorithm.getTimeBetweenRetriesInMillis() != null);
		Assert.assertTrue(algorithm.getTimeBetweenRetriesInMillis().equals(DefaultRetryAlgorithm.DEFAULT_TIME_BETWEEN_RETRIES_IN_MILLIS));
		
		testConstructorValues(null, null, "Null maxNbrRetries");
		testConstructorValues(1, null, "Null timeBetweenRetriesInMillis");
		testConstructorValues(-1, -1L, "-1");
		testConstructorValues(1, -1L, "-1");
	}
	
	private void testConstructorValues(Integer maxRetries, Long timeInMillis, String testMessage) {
		try {new DefaultRetryAlgorithm(maxRetries, timeInMillis);}
		catch (Exception e) {
			Assert.assertTrue(e.getMessage() != null);
			Assert.assertTrue(e.getMessage().contains(testMessage));
			return;
		}
		Assert.fail("No exception thrown.  openInterval="+maxRetries+"  nbrFailures="+timeInMillis);
	}
	
	@Test
	public void testBasic() throws Exception {
		algorithm = new DefaultRetryAlgorithm(2, 10L);		
		Assert.assertTrue(algorithm.isExecutionAllowed());
		
		for (int i = 0; i < algorithm.getMaxNbrRetries(); i++) {
			algorithm.reportExecutionFailure(new Exception("break"));
		}
		Assert.assertTrue(!algorithm.isExecutionAllowed());
		Assert.assertTrue(algorithm.getTimeLastFailureInMillis() > 0);
		
		algorithm.reset();
		algorithm.reportExecutionFailure(new Exception("break"));
		long beginTimeInMillis=System.currentTimeMillis();
		Assert.assertTrue(algorithm.isExecutionAllowed());
		Assert.assertTrue(System.currentTimeMillis() - beginTimeInMillis >= algorithm.getTimeBetweenRetriesInMillis());
		
		algorithm.reset();
		algorithm.reportExecutionFailure(new Exception("break"));
		Thread.sleep(algorithm.getTimeBetweenRetriesInMillis()+1);
		beginTimeInMillis=System.currentTimeMillis();
		Assert.assertTrue(algorithm.isExecutionAllowed());
		Assert.assertTrue(System.currentTimeMillis() - beginTimeInMillis <= 1);
		
	}

}
