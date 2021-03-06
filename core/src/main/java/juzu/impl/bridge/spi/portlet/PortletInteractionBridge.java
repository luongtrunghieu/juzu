/*
 * Copyright 2013 eXo Platform SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package juzu.impl.bridge.spi.portlet;

import juzu.EventQueue;
import juzu.bridge.portlet.JuzuPortlet;
import juzu.impl.bridge.Bridge;
import juzu.request.Result;
import juzu.request.ResponseParameter;
import juzu.impl.plugin.controller.ControllerPlugin;
import juzu.impl.request.ContextualParameter;
import juzu.impl.request.Method;
import juzu.request.Phase;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public abstract class PortletInteractionBridge<Rq extends PortletRequest, Rs extends StateAwareResponse> extends PortletRequestBridge<Rq, Rs> {

  protected PortletInteractionBridge(Bridge bridge, Phase phase, Rq req, Rs resp, PortletConfig config) {
    super(bridge, phase, req, resp, config);
  }

  protected PortletInteractionBridge(Bridge bridge, Phase phase, Rq req, Rs resp, PortletConfig config, Method<?> target, Map<String, String[]> parameters) {
    super(bridge, phase, req, resp, config, target, parameters);
  }

  @Override
  public Map<ContextualParameter, Object> getContextualArguments(Set<ContextualParameter> parameters) {
    Map<ContextualParameter, Object> args = Collections.emptyMap();
    for (ContextualParameter parameter : parameters) {
      if (EventQueue.class.isAssignableFrom(parameter.getType())) {
        PortletEventProducer producer = new PortletEventProducer();
        if (args.isEmpty()) {
          args = new HashMap<ContextualParameter, Object>();
        }
        args.put(parameter, producer);
      }
    }
    return args;
  }

  @Override
  public void send() throws IOException, PortletException {
    if (result instanceof Result.View) {

      //
      Result.View view = (Result.View)result;
      Phase.View.Dispatch update = (Phase.View.Dispatch)view.dispatch;

      //
      Map<String, ResponseParameter> parameters = update.getParameters();
      for (ResponseParameter entry : parameters.values()) {
        super.resp.setRenderParameter(entry.getName(), entry.toArray());
      }

      //
      Method method = bridge.getApplication().resolveBean(ControllerPlugin.class).getDescriptor().getMethodByHandle(update.getTarget());

      // Method id
      super.resp.setRenderParameter("juzu.op", method.getId());

      //
      PortletMode portletMode = update.getProperties().getValue(JuzuPortlet.PORTLET_MODE);
      if (portletMode != null) {
        try {
          super.resp.setPortletMode(portletMode);
        }
        catch (PortletModeException e) {
          throw new IllegalArgumentException(e);
        }
      }

      //
      WindowState windowState = update.getProperties().getValue(JuzuPortlet.WINDOW_STATE);
      if (windowState != null) {
        try {
          super.resp.setWindowState(windowState);
        }
        catch (WindowStateException e) {
          throw new IllegalArgumentException(e);
        }
      }
    } else if (result instanceof Result.Error) {
      Result.Error error = (Result.Error)result;
      throw new PortletException(error.cause);
    }
  }
}
