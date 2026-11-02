package io.github.aedev.flow.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ViewList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.aedev.flow.R
import io.github.aedev.flow.ui.components.shared.FlowSearchField

/**
 * The Search tab's bar, laid out the way YouTube lays its own out: the back affordance and the mic
 * sit outside the field, and the field itself is a slim pill carrying only the query and its clear
 * button.
 *
 * Back always leaves the screen. There is no collapse state to fall into first — the suggestions
 * list is part of this screen, not a surface stacked on top of it.
 *
 * The shell's scaffold already insets its content for the status bar, so this row adds none.
 */
@Composable
fun SearchTopBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
    onVoiceSearch: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onFieldFocused: () -> Unit = {},
    actions: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = BarHorizontalPadding, vertical = BarVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ItemSpacing),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(ActionSize)) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.btn_back),
                modifier = Modifier.size(ActionIconSize),
            )
        }

        FlowSearchField(
            state = textFieldState,
            placeholder = stringResource(R.string.search_videos_channels_placeholder),
            modifier = Modifier.weight(1f),
            onSearch = onSearch,
            focusRequester = focusRequester,
            onFieldFocused = onFieldFocused,
        )

        IconButton(onClick = onVoiceSearch, modifier = Modifier.size(ActionSize)) {
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = stringResource(R.string.voice_search_cd),
                modifier = Modifier.size(ActionIconSize),
            )
        }

        actions?.invoke()
    }
}

/** The filter and layout affordances, shown only once results are on screen. */
@Composable
fun SearchTopBarActions(
    activeFilterCount: Int,
    isGridMode: Boolean,
    onOpenFilters: () -> Unit,
    onToggleGridMode: () -> Unit,
) {
    BadgedBox(
        badge = { if (activeFilterCount > 0) Badge { Text(activeFilterCount.toString()) } },
    ) {
        IconButton(onClick = onOpenFilters, modifier = Modifier.size(ActionSize)) {
            Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = stringResource(R.string.search_filters_title),
                modifier = Modifier.size(ActionIconSize),
            )
        }
    }
    IconButton(onClick = onToggleGridMode, modifier = Modifier.size(ActionSize)) {
        Icon(
            imageVector = if (isGridMode) Icons.Rounded.ViewList else Icons.Rounded.GridView,
            contentDescription = stringResource(R.string.search_toggle_view_mode),
            modifier = Modifier.size(ActionIconSize),
        )
    }
}

private val BarHorizontalPadding = 16.dp
private val BarVerticalPadding = 4.dp
private val ItemSpacing = 4.dp
private val ActionSize = 56.dp
private val ActionIconSize = 32.dp
