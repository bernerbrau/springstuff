package org.vumc.continuum

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.tmatesoft.svn.core.internal.wc.*
import org.tmatesoft.svn.core.wc.*
import org.tmatesoft.svn.core.*

class SvnCheckout extends DefaultTask {
    public String svnUrl
    public String projectDir

    @TaskAction
    def run() {
        println("Checking out ${svnUrl} into ${projectDir}")
        def rev = SVNRevision.HEAD
        def repoUrl
        try {
            repoUrl = SVNURL.parseURIEncoded(svnUrl)
        } catch (SVNException e) {
            throw new InvalidUserDataException("Invalid svnUrl value: $svnUrl", e)
        }

        def dir = new File(projectDir)
        def performUpdate = false
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new InvalidUserDataException("workspaceDir $dir.absolutePath must be a directory")
            }
            if (dir.list()) {
                performUpdate = true
            }
        }
        try {
            def clientManager = SVNClientManager.newInstance(
                    // create a local SVN config dir to make sure we don't reuse existing credentials:
                    new DefaultSVNOptions(new File("${System.getProperty("user.home")}/.subversion"), false))

            if (performUpdate) {
                clientManager.updateClient.doUpdate(dir, rev, SVNDepth.INFINITY, false, false)
            } else {
                clientManager.updateClient.doCheckout(repoUrl, dir, SVNRevision.UNDEFINED, rev, SVNDepth.INFINITY, false)
            }
        } catch (SVNException e) {
            throw new InvalidUserDataException((performUpdate ? "svn-update" : "svn-checkout") + " failed for $svnUrl\n" + e.message, e)
        }
    }
}
