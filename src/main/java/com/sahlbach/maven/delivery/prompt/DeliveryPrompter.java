/*
 * Copyright 2011$ Andreas Sahlbach
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

package com.sahlbach.maven.delivery.prompt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.util.StringUtils;

public class DeliveryPrompter implements Prompter {

    public String prompt(String message) throws PrompterException {
        System.out.print(message);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            return in.readLine();

        } catch (IOException e) {
            throw new PrompterException("Failed to read user response", e);
        }
    }

    public String prompt(String message, String defaultReply) throws PrompterException {
        try {
            writePrompt(formatMessage(message, null, defaultReply));

        } catch (IOException e) {
            throw new PrompterException("Failed to present prompt", e);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = in.readLine();

            if (StringUtils.isEmpty(line)) {
                line = defaultReply;
            }

            return line;
        } catch (IOException e) {
            throw new PrompterException("Failed to read user response", e);
        }
    }

    public String prompt(String message, List possibleValues, String defaultReply) throws PrompterException {
        String formattedMessage = formatMessage(message, possibleValues, defaultReply);

        String line;

        do {
            try {
                writePrompt(formattedMessage);
            } catch (IOException e) {
                throw new PrompterException("Failed to present prompt", e);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            try {
                line = in.readLine();
            } catch (IOException e) {
                throw new PrompterException("Failed to read user response", e);
            }

            if (StringUtils.isEmpty(line)) {
                line = defaultReply;
            }

            if (line != null && !possibleValues.contains(line)) {
                System.out.println("Invalid selection.");
            }
        } while (line == null || !possibleValues.contains(line));

        return line;
    }

    public String prompt(String message, List possibleValues) throws PrompterException {
        return prompt(message, possibleValues, null);
    }

    private String formatMessage(String message, List possibleValues, String defaultReply) {
        StringBuilder formatted = new StringBuilder(message.length() * 2);

        formatted.append(message);

        if (possibleValues != null && !possibleValues.isEmpty()) {
            formatted.append(" (");

            for (Iterator it = possibleValues.iterator(); it.hasNext(); ) {
                String possibleValue = (String) it.next();

                formatted.append(possibleValue);

                if (it.hasNext()) {
                    formatted.append('/');
                }
            }

            formatted.append(')');
        }

        if (defaultReply != null) {
            formatted.append(' ').append(defaultReply).append(": ");
        }

        return formatted.toString();
    }

    private void writePrompt(String message) throws IOException {
        System.out.print(message + ": ");
    }

    public void showMessage(String message) throws PrompterException {
        try {
            writePrompt(message);
        } catch (IOException e) {
            throw new PrompterException("Failed to present prompt", e);
        }

    }

    public String promptForPassword(String message) throws PrompterException {
        try {
            writePrompt(message);
        } catch (IOException e) {
            throw new PrompterException("Failed to present prompt", e);
        }

        try {
            // TODO: with JDK 1.6, we could call System.console().readPassword(message, null);

            BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
            while ( System.in.available() != 0 ) {
                // empty input queue
                //noinspection ResultOfMethodCallIgnored
                System.in.read();
            }
            MaskingThread thread = new MaskingThread();
            thread.start();

            String pass = in.readLine();

            // stop masking
            thread.stopMasking();

            return pass;
        } catch (IOException e) {
            throw new PrompterException("Failed to read user response", e);
        }
    }

    // based on ideas from http://java.sun.com/developer/technicalArticles/Security/pwordmask/
    // and maven-gpg-plugin
    private class MaskingThread extends Thread {
        private volatile boolean stop;

        /**
         * Begin masking until asked to stop.
         */
        @Override
        public void run() {
            // this needs to be high priority to make sure the characters don't
            // really get to the screen.

            int priority = Thread.currentThread().getPriority();
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            try {
                stop = false;
                while (!stop) {
                    // print a backspace + * to overwrite anything they type
                    System.out.print("\010*");
                    try {
                        // attempt masking at this rate
                        Thread.sleep(1);
                    } catch (InterruptedException iex) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            } finally {
                // restore the original priority
                Thread.currentThread().setPriority(priority);
            }
        }

        /**
         * Instruct the thread to stop masking.
         */
        public void stopMasking() {
            this.stop = true;
        }
    }
}
