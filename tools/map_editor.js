const DEFAULT_SIZE = 48
const ID_MAP = "map"
const Modes = Object.freeze({
    TILES: Symbol("tiles"),
    OBJECTS: Symbol("objects")
});
const ElementsDefinitions = Object.freeze([
    "PLAYER",
    "PALM_TREE",
    "ROCK",
    "BUILDING",
    "FENCE",
    "LIGHT_POLE",
    "BARRIER",
    "CABIN",
    "CAR",
    "GUARD_HOUSE",
    "ANTENNA",
]);
const TILE_SAND = "#886A08"
const TILE_ROAD = "#424242"
const body = document.body;
const table = document.getElementById(ID_MAP).appendChild(document.createElement('table'));
const RADIO_GROUP_NAME_MODES = "modes"
const OPTION_MODE_OBJECTS = "option_mode_objects"
const DIV_ID_LEFT_MENU = "left_menu_div"
const CLASS_NAME_GAME_OBJECT_SELECTION = "game_object_selection";
const CLASS_NAME_GAME_OBJECT_RADIO = "game_object_radio";
const RADIO_GROUP_NAME_GAME_OBJECT_SELECTIONS = "game_object_selections";

class CellData {
    constructor() {
        this.tile = "#00FF00"
        this.object = null
    }
}

class MapEditor {

    constructor() {
        this.tableCreate();
        this.inflateElementsLeftMenu();
        table.style.width = DEFAULT_SIZE * 64 + 'px';
        table.style.height = DEFAULT_SIZE * 64 + 'px';
    }

    inflateElementsLeftMenu() {
        var leftMenu = document.getElementById(DIV_ID_LEFT_MENU);
        ElementsDefinitions.forEach(element => {
            var div = document.createElement("div");
            var radioButton = addRadioButtonForElement(div);
            div.className = CLASS_NAME_GAME_OBJECT_SELECTION;
            var label = document.createElement("label");
            label.for = radioButton.id;
            label.appendChild(document.createTextNode(element));
            div.appendChild(label);
            leftMenu.appendChild(div);

            function addRadioButtonForElement(div) {
                var radioButton = document.createElement("input");
                radioButton.type = "radio";
                radioButton.className = CLASS_NAME_GAME_OBJECT_RADIO;
                radioButton.name = RADIO_GROUP_NAME_GAME_OBJECT_SELECTIONS;
                radioButton.value = element;
                radioButton.id = "element_selection_" + element;
                div.appendChild(radioButton);
                return radioButton;
            }
        });
    }

    findChildTextNode(cell) {
        var textNode = null
        for (var i = 0; i < cell.childNodes.length; i++) {
            var curNode = cell.childNodes[i];
            if (curNode.nodeName === "#text") {
                textNode = curNode;
                break;
            }
        }
        return textNode
    }

    placeElementInCell(cell, cellData, selectedMode, input) {
        var leftClick = input == "click";
        if (selectedMode == Modes.TILES) {
            applyTileChangeInCell(leftClick, cellData, cell);
        } else if (selectedMode == Modes.OBJECTS) {
            if (leftClick) {
                placeElementObject(this, cell);
            } else {
                removeElementObject(editor, cell);
            }
        }

        function applyTileChangeInCell(leftClick, cellData, cell) {
            var selectedTile = leftClick ? TILE_ROAD : TILE_SAND;
            cellData.tile = selectedTile;
            cell.style.backgroundColor = selectedTile;
        }

        function placeElementObject(editor, cell) {
            var textNode = editor.getOrAddChildTextNode(cell);
            var selection = document.querySelector('input[name="' + RADIO_GROUP_NAME_GAME_OBJECT_SELECTIONS + '"]:checked').value;
            cellData.object = selection;
            textNode.nodeValue = selection;
        }

        function removeElementObject(editor, cell) {
            var textNode = editor.getOrAddChildTextNode(cell);
            cellData.object = null;
            textNode.nodeValue = null;
        }
    }

    onCellLeftClicked(row, col) {
        var cell = table.rows[row].cells[col]
        initializeCellData(cell);
        var selectedMode = Modes[document.querySelector('input[name="' + RADIO_GROUP_NAME_MODES + '"]:checked').value]
        var cellData = cell.cellData;
        this.placeElementInCell(cell, cellData, selectedMode, "click")
    }

    onCellRightClicked(row, col) {
        var cell = table.rows[row].cells[col]
        initializeCellData(cell);
        var cellData = cell.cellData;
        var selectedMode = Modes[document.querySelector('input[name="' + RADIO_GROUP_NAME_MODES + '"]:checked').value]
        this.placeElementInCell(cell, cellData, selectedMode, "contextmenu")
    }

    getOrAddChildTextNode(cell) {
        var textNode = this.findChildTextNode(cell);
        if (textNode == null) {
            textNode = document.createTextNode("#");
            cell.appendChild(textNode);
        }
        return textNode
    }

    tableCreate() {
        for (let i = 0; i < DEFAULT_SIZE; i++) {
            const tr = table.insertRow();
            for (let j = 0; j < DEFAULT_SIZE; j++) {
                const td = tr.insertCell();
                td.addEventListener('click', e => {
                    this.onCellLeftClicked(i, j);
                })
                td.addEventListener('contextmenu', e => {
                    e.preventDefault();
                    this.onCellRightClicked(i, j);
                    return false;
                }, false)
            }
        }
        this.initializeModesRadioButtons();
    }

    initializeModesRadioButtons() {
        const leftMenuDiv = document.getElementById(DIV_ID_LEFT_MENU).style;
        function onRadioButtonChecked(event) {
            if (event.target.id == OPTION_MODE_OBJECTS) {
                leftMenuDiv.visibility = 'visible'
            } else {
                leftMenuDiv.visibility = 'hidden'
            }
        }
        document.querySelectorAll("input[name='" + RADIO_GROUP_NAME_MODES + "']").forEach((input) => {
            input.addEventListener('change', onRadioButtonChecked);
        });
    }
}

function initializeCellData(cell) {
    if (cell.cellData == null) {
        cell.cellData = new CellData();
    }
}