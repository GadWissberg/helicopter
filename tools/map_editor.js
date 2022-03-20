const DEFAULT_SIZE = 48
const ID_MAP = "map"
const Modes = Object.freeze({
    TILES: Symbol("tiles"),
    OBJECTS: Symbol("objects")
});
const body = document.body;
const table = document.getElementById(ID_MAP).appendChild(document.createElement('table'));
const MODES_GROUP_NAME = "modes"

class CellData {
    constructor() {
        this.tile = null
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
    placeElementInCell(cellData, textNode) {
        if (selectedMode == Modes.TILES) {
            cellData.tile = "#"
            textNode = "#"
        } else if (selectedMode == Modes.OBJECTS) {
            cellData.object = "0"
            // cell.appendChild(document.createTextNode)
        }
    }
    onCellClicked(row, col) {
        var cell = table.rows[row].cells[col]
        if (cell.cellData == null) {
            cell.cellData = new CellData()
        }
        var cellData = cell.cellData;
        selectedMode = Modes[document.querySelector('input[name="' + MODES_GROUP_NAME + '"]:checked').value]
        textNode = this.getOrAddChildTextNode(cell)
        this.placeElementInCell(cellData, textNode)
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
                    this.onCellClicked(i, j)
                })
            }
        }
        this.initializeModesRadioButtons();
    }

    initializeModesRadioButtons() {
        var rad = document.myForm.myRadios;
        var prev = null;
        for (var i = 0; i < rad.length; i++) {
            rad[i].addEventListener('change', function () {
                (prev) ? console.log(prev.value) : null;
                if (this !== prev) {
                    prev = this;
                }
                console.log(this.value);
            });
        }
    }
}