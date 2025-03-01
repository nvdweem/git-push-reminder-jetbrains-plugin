package com.chriscarini.jetbrains.gitpushreminder;

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.chriscarini.jetbrains.gitpushreminder.messages.PluginMessages;
import com.chriscarini.jetbrains.gitpushreminder.settings.SettingsManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectCloseHandler;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;

public class GitPushReminder implements ProjectCloseHandler {

    @Override
    public boolean canClose(@NotNull Project project) {
        final List<GitHelper.RepositoryAndBranch> branchesWithUnpushedCommits = GitHelper.getBranchesWithUnpushedCommits(project, SettingsManager.getInstance().getState().checkAllBranches);

        // If there are *NO* branches with outgoing/un-pushed commits, then we can close.
        boolean canClose = branchesWithUnpushedCommits.isEmpty();

        if (!canClose && SettingsManager.getInstance().getState().showDialog) {
            final int dialogResult = Messages.showOkCancelDialog(project,
                PluginMessages.get("git.push.reminder.closing.dialog.body.unpushed.branches",
                    branchesWithUnpushedCommits.stream()
                        .map(repoAndBranch -> repoAndBranch.branch().getName())
                        .sorted()
                        .collect(Collectors.joining("</li><li>"))
                ),
                PluginMessages.get("git.push.reminder.closing.dialog.title"),
                PluginMessages.get("git.push.reminder.closing.dialog.button.close.anyway"),
                PluginMessages.get("git.push.reminder.closing.dialog.button.keep.project.open"),
                Messages.getWarningIcon()
            );

            return dialogResult == MessageConstants.OK;
        }

        return true;
    }
}
