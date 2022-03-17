const DEFAULT_SIZE = 48
const ID_MAP = "map"
const Modes = Object.freeze({
    TILES:   Symbol("tiles"),
    OBJECTS:  Symbol("objects")
});
const body = document.body;
const table = document.getElementById(ID_MAP).appendChild(document.createElement('table'));
const MODES_GROUP_NAME = "modes"
class CellData {
}

function onCellClicked(row, col){
    var cell = table.rows[row].cells[col]
        if (cell.cellData == null){
            cell.cellData = new CellData()
        }
    }
    var cellData = cell.cellData;
    selectedMode = document.querySelector('input[name="'+MODES_GROUP_NAME+'"]:checked').value
    if (selectedMode == Modes.TILES){
        cellData.tile = "#"
        alert("tiles")
    } else if (selectedMode == Modes.OBJECTS){
        cellData.tile = "0"
        alert("objects")
    }
    alert("row:" +row +", col:" + col)
}

function tableCreate() {

  for (let i = 0; i < DEFAULT_SIZE; i++) {
    const tr = table.insertRow();
    for (let j = 0; j < DEFAULT_SIZE; j++) {
        const td = tr.insertCell();
//        td.appendChild(document.createTextNode("&nbsp;"));
        td.addEventListener('click', e=> {
            onCellClicked(i, j)
        })
    }
  }

}

tableCreate();