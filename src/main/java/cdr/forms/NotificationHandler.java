/**
 * Copyright 2010 The University of North Carolina at Chapel Hill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cdr.forms;

import java.util.List;

import crosswalk.Form;

public interface NotificationHandler {

	/**
	 * Send notifications for a successful deposit, including receipt if depositorEmail is specified and
	 * notices to those specified in the form itself.
	 * @param deposit the deposit
	 * @param result the result object
	 */
	public void notifyDeposit(Deposit deposit, DepositResult result);

	/**
	 * Send unrecoverable error notification to the administrator.
	 * @param deposit the deposit
	 * @param result the result object
	 */
	public void notifyError(Deposit deposit, DepositResult result);

}
