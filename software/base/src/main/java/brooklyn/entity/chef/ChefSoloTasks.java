/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package brooklyn.entity.chef;

import java.util.Map;

import brooklyn.entity.Entity;
import brooklyn.entity.effector.EffectorTasks;
import brooklyn.entity.software.SshEffectorTasks;
import brooklyn.management.TaskFactory;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.net.Urls;
import brooklyn.util.ssh.BashCommands;
import brooklyn.util.task.DynamicTasks;
import brooklyn.util.task.Tasks;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Beta
public class ChefSoloTasks {

    public static TaskFactory<?> installChef(String chefDirectory, boolean force) {
        // TODO check on entity whether it is chef _server_
        String installCmd = cdAndRun(chefDirectory, ChefBashCommands.INSTALL_FROM_OPSCODE);
        if (!force) installCmd = BashCommands.alternatives("which chef-solo", installCmd);
        return SshEffectorTasks.ssh(installCmd).summary("install chef");
    }

    public static TaskFactory<?> installCookbooks(final String chefDirectory, final Map<String,String> cookbooksAndUrls, final boolean force) {
        return Tasks.<Void>builder().name("install cookbooks").body(
                new Runnable() {
                    public void run() {
                        Entity e = EffectorTasks.findEntity();
                        if (cookbooksAndUrls==null)
                            throw new IllegalStateException("No cookbooks defined to install at "+e);
                        for (String cookbook: cookbooksAndUrls.keySet())
                            DynamicTasks.queue(installCookbook(chefDirectory, cookbook, cookbooksAndUrls.get(cookbook), force));
                    }
                }).buildFactory();
    }

    public static TaskFactory<?> installCookbook(String chefDirectory, String cookbook, String url, boolean force) {
        // TODO if it's server, try knife first
        // TODO support downloads from classpath / local server
        return SshEffectorTasks.ssh(cdAndRun(chefDirectory, ChefBashCommands.downloadAndExpandCookbook(url, cookbook, force))).
                summary("install cookbook "+cookbook).requiringExitCodeZero();
    }

    protected static String cdAndRun(String targetDirectory, String command) {
        return BashCommands.chain("mkdir -p "+targetDirectory,
                "cd "+targetDirectory,
                command);
    }

    public static TaskFactory<?> buildChefFile(String runDirectory, String chefDirectory, String phase, Iterable<? extends String> runList,
            Map<String, Object> optionalAttributes) {
        // TODO if it's server, try knife first
        // TODO configure add'l properties
        String phaseRb = 
                "root = File.absolute_path(File.dirname(__FILE__))\n"+
                "\n"+
                "file_cache_path root\n"+
//                "cookbook_path root + '/cookbooks'\n";
                "cookbook_path '"+chefDirectory+"'\n";

        Map<String,Object> phaseJsonMap = MutableMap.of();
        if (optionalAttributes!=null)
            phaseJsonMap.putAll(optionalAttributes);
        if (runList!=null)
            phaseJsonMap.put("run_list", ImmutableList.copyOf(runList));
        Gson json = new GsonBuilder().create();
        String phaseJson = json.toJson(phaseJsonMap);

        return Tasks.sequential("build chef files for "+phase,
                    SshEffectorTasks.put(Urls.mergePaths(runDirectory)+"/"+phase+".rb").contents(phaseRb).createDirectory(),
                    SshEffectorTasks.put(Urls.mergePaths(runDirectory)+"/"+phase+".json").contents(phaseJson));
    }

    public static TaskFactory<?> runChef(String runDir, String phase) {
        return runChef(runDir, phase, false);
    }
    /** see {@link ChefConfig#CHEF_RUN_CONVERGE_TWICE} for background on why 'twice' is available */
    public static TaskFactory<?> runChef(String runDir, String phase, Boolean twice) {
        String cmd = "sudo chef-solo -c "+phase+".rb -j "+phase+".json -ldebug";
        if (twice!=null && twice) cmd = BashCommands.alternatives(cmd, cmd);

        return SshEffectorTasks.ssh(cdAndRun(runDir, cmd)).
                summary("run chef for "+phase).requiringExitCodeZero();
    }
    
}
