const DEFAULT_SIZE = 48
const ID_MAP = "map"
const Modes = Object.freeze({
    TILES: Symbol("tiles"),
    OBJECTS: Symbol("objects")
});
const TILE_SAND = "#886A08"
const TILE_ROAD = "#424242"
const body = document.body;
const table = document.getElementById(ID_MAP).appendChild(document.createElement('table'));
const MODES_GROUP_NAME = "modes"
const OPTION_MODE_OBJECTS = "option_mode_objects"
const DIV_ID_LEFT_MENU = "left_menu"

class CellData {
    constructor() {
        this.tile = "#00FF00"
        this.object = null
    }
}

class MapEditor {

    constructor() {
        this.tableCreate();
    }
    findChildTextNode(cell) {
        var textNode = null
        for (var i = 0; i < cell.childNodes.length; i++) {
            var curNode = cell.childNodes[i];
            if (curNode.nodeName === "#text") {
                textNode = curNode.nodeValue;
                break;
            }
        }
        return textNode
    }
    placeElementInCell(cell, cellData, selectedMode, input) {
        if (selectedMode == Modes.TILES) {
            var selectedTile = input == "click" ? TILE_ROAD : TILE_SAND
            cellData.tile = selectedTile
            cell.style.backgroundColor = selectedTile
        } else if (selectedMode == Modes.OBJECTS) {
            var textNode = this.getOrAddChildTextNode(cell)
            cellData.object = "0"
            textNode = "0"
        }
    }

    onCellLeftClicked(row, col) {
        var cell = table.rows[row].cells[col]
        initializeCellData(cell);
        var selectedMode = Modes[document.querySelector('input[name="' + MODES_GROUP_NAME + '"]:checked').value]
        var cellData = cell.cellData;
        this.placeElementInCell(cell, cellData, selectedMode, "click")
    }

    onCellRightClicked(row, col) {
        var cell = table.rows[row].cells[col]
        initializeCellData(cell);
        var cellData = cell.cellData;
        var selectedMode = Modes[document.querySelector('input[name="' + MODES_GROUP_NAME + '"]:checked').value]
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
                    this.onCellLeftClicked(i, j)
                })
                td.addEventListener('contextmenu', e => {
                    e.preventDefault()
                    this.onCellRightClicked(i, j)
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
        document.querySelectorAll("input[name='" + MODES_GROUP_NAME + "']").forEach((input) => {
            input.addEventListener('change', onRadioButtonChecked);
        });
    }
}

function initializeCellData(cell) {
    if (cell.cellData == null) {
        cell.cellData = new CellData();
    }
}
