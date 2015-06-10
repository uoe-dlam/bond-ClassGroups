function handleOverriddenUserSelect(selections) {
    $('leaderOverriddenTrue').checked = true;
    if(selections.length > 0) {
        $('overriddenLeaderDisplay').update(selections.first().displayName);
        $('newOverriddenLeader').value = selections.first().pk1;
    }
}l