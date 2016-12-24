package com.example.zach.scorekeeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private ArrayList<Player> players;
    private Player selectedPlayer;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////HELPERS//////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void addPoints() {
        EditText pointsToAdd = (EditText) findViewById(R.id.pointsToAdd);
        Editable points = pointsToAdd.getText();

        if (selectedPlayer != null && null != points && !"".equals(points.toString())) {
            try {
                Integer newScore = selectedPlayer.getCurrentScore() + Integer.valueOf(points.toString());
                selectedPlayer.setCurrentScore(newScore);
                pointsToAdd.setText(R.string.empty);

                TextView selectedPlayerScore = (TextView) findViewById(R.id.selectedPlayerScore);
                selectedPlayerScore.setText(String.format(Locale.US, "%d", newScore));

                LinearLayout playerTile = (LinearLayout) findViewById(selectedPlayer.getId());

                if (null != playerTile) {
                    TextView playerScore = (TextView) playerTile.getChildAt(1);
                    playerScore.setText(String.format(Locale.US, "%d", newScore));
                }
            } catch (NumberFormatException e) {
                pointsToAdd.setText(R.string.empty);
                Toast.makeText(getApplicationContext(), R.string.number_too_big, Toast.LENGTH_LONG).show();
            }

        }
    }

    private LinearLayout addPlayerTile(Player player) {
        ViewGroup parent = (ViewGroup) findViewById(R.id.players);
        LinearLayout tile = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.tile, parent, false);
        tile.setId(player.getId());
        parent.addView(tile);

        tile.setOnClickListener(playerSelectedClickListener());

        TextView tilePlayerName = (TextView) tile.findViewById(R.id.tilePlayerName);
        tilePlayerName.setText(player.getName());

        TextView tilePlayerScore = (TextView) tile.findViewById(R.id.tilePlayerScore);
        tilePlayerScore.setText(String.format(Locale.US, "%d", player.getCurrentScore()));

        registerForContextMenu(tile);

        return tile;
    }

    private void createTiles() {
        LinearLayout tiles = (LinearLayout) findViewById(R.id.players);

        if (tiles.getChildCount() > 0) {
            tiles.removeAllViews();
        }

        for (Player player : players) {
            addPlayerTile(player);
        }
    }

    private void initPlayer() {
        TextView selectedPlayerName = (TextView) findViewById(R.id.selectedPlayerName);
        TextView selectedPlayerScore = (TextView) findViewById(R.id.selectedPlayerScore);
        EditText pointsToAdd = (EditText) findViewById(R.id.pointsToAdd);

        if (null == selectedPlayer) {
            selectedPlayerName.setText(R.string.empty);
            selectedPlayerScore.setText(R.string.empty);
            pointsToAdd.setText(R.string.empty);
        } else {
            selectedPlayerName.setText(selectedPlayer.getName());
            selectedPlayerScore.setText(String.format(Locale.US, "%d", selectedPlayer.getCurrentScore()));
            pointsToAdd.setText(R.string.empty);
        }
    }

    private void nextPlayer() {
        int index = -1;
        if (selectedPlayer != null) {
            for (int i = 0; i < players.size(); i++) {
                if (selectedPlayer.getId().equals(players.get(i).getId())) {
                    index = i;
                }
            }
            if (index >= 0) {
                if (index == players.size()-1) {
                    index = 0;
                }
                else {
                    index++;
                }

                selectedPlayer = players.get(index);

                initPlayer();
            }
        }
    }

    private void previousPlayer() {
        int index = -1;
        if (selectedPlayer != null) {
            for (int i = 0; i < players.size(); i++) {
                if (selectedPlayer.getId().equals(players.get(i).getId())) {
                    index = i;
                }
            }
            if (index >= 0) {
                if (index == 0) {
                    index = players.size()-1;
                }
                else {
                    index--;
                }

                selectedPlayer = players.get(index);

                initPlayer();
            }
        }
    }

    private void registerListeners() {
        EditText pointsToAdd = (EditText) findViewById(R.id.pointsToAdd);
        pointsToAdd.setOnKeyListener(addPointsEnterKeyListener());

        Button addPoints = (Button) findViewById(R.id.addPoints);
        addPoints.setOnClickListener(addPointsClickListener());

        RelativeLayout playerLayout = (RelativeLayout) findViewById(R.id.player);
        playerLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {

            @Override
            public void onSwipeLeft() {
                nextPlayer();
            }

            @Override
            public void onSwipeRight() {
                previousPlayer();
            }
        });
    }

    private void initData() {
        if (players.size() > 0) {
            if (null == selectedPlayer) {
                selectedPlayer = players.get(0);
            }

        } else {
            selectedPlayer = null;
            showAddPlayerDialog(true);
        }

        initPlayer();
    }

    private void extractState(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            players = savedInstanceState.getParcelableArrayList("players");
            selectedPlayer = savedInstanceState.getParcelable("selectedPlayer");
        }

        if (null == players) {
            players = new ArrayList<>();
        }
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////LISTENERS////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnKeyListener newPlayerNameOnKeyListener(final Dialog dialog, final CheckBox addAnotherNewPlayerCb, final boolean defaultAddAnother) {
        return new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String newPlayerName = ((EditText) v).getText().toString();
                    boolean addAnotherPlayer = addAnotherNewPlayerCb.isChecked();

                    Player player = new Player(View.generateViewId(), newPlayerName, 0);
                    addPlayer(player, addAnotherPlayer, defaultAddAnother);
                    dialog.dismiss();
                    return false;
                }

                return false;
            }
        };
    }

    private View.OnKeyListener addPointsEnterKeyListener() {
        return new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    addPoints();
                    return false;
                }

                return false;
            }
        };
    }

    private View.OnClickListener addPointsClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPoints();
            }
        };
    }

    private View.OnClickListener playerSelectedClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Player player : players) {
                    if (v.getId() == player.getId()) {
                        selectedPlayer = player;
                    }
                }

                initPlayer();
            }
        };
    }






    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////MENU LISTENERS/////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private MenuItem.OnMenuItemClickListener newGameMenuItemClickListener() {
        return new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showNewGameDialog();
                return true;
            }
        };
    }

    private MenuItem.OnMenuItemClickListener addPlayerMenuItemClickListener() {
        return new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAddPlayerDialog(false);
                return true;
            }
        };
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////DIALOG LISTENERS////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private DialogInterface.OnClickListener newGameDialogListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        handleNewGameDialog();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }

            private void handleNewGameDialog() {
                for (Player player : players) {
                    player.setCurrentScore(0);
                }

                EditText pointsToAdd = (EditText) findViewById(R.id.pointsToAdd);
                pointsToAdd.setText(R.string.empty);

                TextView selectedPlayerScore = (TextView) findViewById(R.id.selectedPlayerScore);
                selectedPlayerScore.setText(R.string.zero);

                createTiles();
            }
        };
    }

    private DialogInterface.OnClickListener deletePlayerDialogListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        handleDeletePlayerDialog();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }

            private void handleDeletePlayerDialog() {
                if (null != selectedPlayer) {
                    LinearLayout selectedTile = (LinearLayout) findViewById(selectedPlayer.getId());
                    LinearLayout tiles = (LinearLayout) selectedTile.getParent();
                    tiles.removeView(selectedTile);

                    String deletedPlayerName = selectedPlayer.getName();

                    players.remove(selectedPlayer);

                    if (players.size() > 0) {
                        selectedPlayer = players.get(0);
                    } else {
                        selectedPlayer = null;
                    }

                    initPlayer();

                    Toast.makeText(getApplicationContext(), getString(R.string.player_deleted, deletedPlayerName), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private DialogInterface.OnClickListener addPlayerDialogListener(final EditText newPlayerNameEt, final CheckBox addAnotherNewPlayerCb, final boolean defaultAddAnother) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        handlePositiveButton();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }

            private void handlePositiveButton() {
                String newPlayerName = newPlayerNameEt.getText().toString();
                boolean addAnotherPlayer = addAnotherNewPlayerCb.isChecked();

                Player player = new Player(View.generateViewId(), newPlayerName, 0);
                addPlayer(player, addAnotherPlayer, defaultAddAnother);
            }
        };
    }

    private void addPlayer(Player player, boolean addAnotherPlayer, boolean defaultAddAnother) {
        players.add(player);
        selectedPlayer = player;

        addPlayerTile(player);
        initPlayer();

        Toast.makeText(getApplicationContext(), getString(R.string.player_added, player.getName()), Toast.LENGTH_LONG).show();

        if (addAnotherPlayer) {
            showAddPlayerDialog(defaultAddAnother);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////DIALOGS//////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void deletePlayerDialog() {
        DialogInterface.OnClickListener dialogClickListener = deletePlayerDialogListener();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.confirm_delete_player, selectedPlayer.getName()))
                .setPositiveButton(R.string.positive, dialogClickListener)
                .setNegativeButton(R.string.negative, dialogClickListener).show();
    }

    private void showNewGameDialog() {
        DialogInterface.OnClickListener dialogClickListener = newGameDialogListener();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.confirm_new_game)
                .setPositiveButton(R.string.positive, dialogClickListener)
                .setNegativeButton(R.string.negative, dialogClickListener).show();
    }

    private void showAddPlayerDialog(boolean defaultAddAnother) {

        LinearLayout newPlayerContent = (LinearLayout) LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.add_another_player_dialog, new LinearLayout(MainActivity.this));

        CheckBox addAnotherCheckBox = (CheckBox) newPlayerContent.findViewById(R.id.addAnotherNewPlayer);
        addAnotherCheckBox.setChecked(defaultAddAnother);

        DialogInterface.OnClickListener  dialogClickListener= addPlayerDialogListener(
                (EditText) newPlayerContent.findViewById(R.id.newPlayerName),
                addAnotherCheckBox, defaultAddAnother
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        AlertDialog dialog = builder.setTitle(R.string.enter_player_name)
                .setView(newPlayerContent)
                .setPositiveButton(R.string.done, dialogClickListener)
                .setNegativeButton(R.string.cancel, dialogClickListener).create();

        EditText newPlayerName = (EditText) newPlayerContent.findViewById(R.id.newPlayerName);
        newPlayerName.setOnKeyListener(newPlayerNameOnKeyListener(dialog, addAnotherCheckBox, defaultAddAnother));

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        dialog.show();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////ACTIVITY/////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extractState(savedInstanceState);
        registerListeners();
        createTiles();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);

        MenuItem newGameMenuItem = menu.findItem(R.id.newGameMenuItem);
        newGameMenuItem.setOnMenuItemClickListener(newGameMenuItemClickListener());

        MenuItem addPlayerMenuItem = menu.findItem(R.id.addPlayerMenuItem);
        addPlayerMenuItem.setOnMenuItemClickListener(addPlayerMenuItemClickListener());

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        for (Player player : players) {
            if (v.getId() == player.getId()) {
                selectedPlayer = player;
            }
        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.player_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removePlayerMenuItem:
                deletePlayerDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("players", players);
        outState.putParcelable("selectedPlayer", selectedPlayer);

        super.onSaveInstanceState(outState);
    }

}
