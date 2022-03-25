class CellData {
    constructor() {
        this.tile = "#00FF00"
        this.object = null
    }
}

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
const MAP_SIZES = Object.freeze({ small: 48, medium: 96, large: 192 });
const TILE_SAND = Object.freeze({ tile: "#886A08", symbol: "0" })
const TILE_ROAD = Object.freeze({ tile: "#424242", symbol: "1" });
const body = document.body;
const table = document.getElementById(ID_MAP).appendChild(document.createElement('table'));
const RADIO_GROUP_NAME_MODES = "modes"
const OPTION_MODE_OBJECTS = "option_mode_objects"
const DIV_ID_LEFT_MENU = "left_menu_div"
const CLASS_NAME_GAME_OBJECT_SELECTION = "game_object_selection";
const CLASS_NAME_GAME_OBJECT_RADIO = "game_object_radio";
const RADIO_GROUP_NAME_GAME_OBJECT_SELECTIONS = "game_object_selections";
const DIV_ID_BUTTON_SAVE = "button_save";
const DIV_ID_BUTTON_LOAD = "button_load";
const OUTPUT_FILE_NAME = 'map.json';
const SELECT_ID_DROPDOWN_MAP_SIZES = "dropdown_map_sizes";
class MapEditor {

    constructor() {
        this.resetMap();
        this.initializeModesRadioButtons();
        this.inflateElementsLeftMenu();
        fillMapSizesDropdown();
        table.style.width = this.map_size * 64 + 'px';
        table.style.height = this.map_size * 64 + 'px';
        var self = this;
        defineSaveProcess();
        defineLoadProcess();

        function fillMapSizesDropdown() {
            var dropDown = document.getElementById(SELECT_ID_DROPDOWN_MAP_SIZES);
            for (var size in MAP_SIZES) {
                var option = document.createElement("option");
                option.value = size;
                option.text = size;
                dropDown.appendChild(option);
            }
            dropDown.addEventListener("change", function () {
                self.resetMap(MAP_SIZES[dropDown.value]);
            })
        }

        function defineLoadProcess() {
            document.getElementById(DIV_ID_BUTTON_LOAD).addEventListener('click', () => {

                var input = document.createElement('input');
                input.type = 'file';
                input.addEventListener('change', e => {
                    var file = e.target.files[0];
                    if (!file) {
                        return;
                    }
                    var reader = new FileReader();
                    reader.onload = e => {
                        var contents = e.target.result;
                        var inputMapObject = JSON.parse(contents);
                        self.resetMap(inputMapObject.size);
                        inflateTiles();
                        for (var i = 0; i < inputMapObject.elements.length; i++) {
                            var element = inputMapObject.elements[i];
                            self.placeElementObject(table.rows[element.row].cells[element.col], element.definition);
                        }

                        function inflateTiles() {
                            for (var i = 0; i < inputMapObject.tiles.length; i++) {
                                var cell = table.rows[Math.floor(i / self.map_size)].cells[i % self.map_size];
                                var placedTile = self.findTileBySymbol(inputMapObject.tiles.charAt(i));
                                cell.style.backgroundColor = placedTile.tile;
                                cell.cellData = new CellData();
                                cell.cellData.tile = placedTile;
                            }
                        }
                    };
                    reader.readAsText(file);
                }, false);
                input.click();
            });
        }

        function defineSaveProcess() {
            document.getElementById(DIV_ID_BUTTON_SAVE).addEventListener('click', e => {
                var output = {};
                var tilesString = calculateTilesMapString();
                output.tiles = tilesString;
                var elementsArray = calculateElementsArray();
                output.elements = elementsArray;
                output.size = self.map_size;
                var json = JSON.stringify(output);
                saveJsonToFile(json);

                function calculateElementsArray() {
                    var elementsArray = [];
                    for (var row in table.rows) {
                        for (var col in table.rows[row].cells) {
                            if (table.rows[row].cells[col].cellData != null) {
                                if (table.rows[row].cells[col].cellData.object != null) {
                                    deflateElementObject(elementsArray, row, col);
                                }
                            }
                        }
                    }
                    return elementsArray;

                    function deflateElementObject(elementsArray, row, col) {
                        var elementObject = {};
                        elementObject.definition = table.rows[row].cells[col].cellData.object;
                        elementObject.row = parseInt(row);
                        elementObject.col = parseInt(col);
                        elementsArray.push(elementObject);
                    }
                }

                function calculateTilesMapString() {
                    var tilesString = "";
                    for (var row = 0; row < table.rows.length; row++) {
                        for (var col = 0; col < table.rows[row].cells.length; col++) {
                            var currentTile = TILE_SAND;
                            var cellData = table.rows[row].cells[col].cellData;
                            if (cellData != null && cellData.tile != null) {
                                currentTile = self.findTileBySymbol(cellData.tile.symbol);
                            }
                            tilesString += currentTile.symbol;
                        }
                    }
                    return tilesString;
                }

                function saveJsonToFile(json) {
                    var bb = new Blob([json], { type: 'text/json' });
                    var a = document.createElement('a');
                    a.download = OUTPUT_FILE_NAME;
                    a.href = window.URL.createObjectURL(bb);
                    a.click();
                }


            });
        }
    }

    findTileBySymbol(symbol) {
        var result = TILE_SAND;
        if (symbol == TILE_ROAD.symbol) {
            result = TILE_ROAD;
        }
        return result;
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
        var self = this;
        var leftClick = input == "click";
        if (selectedMode == Modes.TILES) {
            applyTileChangeInCell(leftClick, cellData, cell);
        } else if (selectedMode == Modes.OBJECTS) {
            applyElementChangeInCell();
        }

        function applyElementChangeInCell() {
            if (leftClick) {
                var selection = document.querySelector('input[name="' + RADIO_GROUP_NAME_GAME_OBJECT_SELECTIONS + '"]:checked').value;
                self.placeElementObject(cell, selection);
            } else {
                removeElementObject(editor, cell);
            }
        }

        function applyTileChangeInCell(leftClick, cellData, cell) {
            var selectedTile = leftClick ? TILE_ROAD : TILE_SAND;
            cellData.tile = selectedTile;
            cell.style.backgroundColor = selectedTile.tile;
        }


        function removeElementObject(editor, cell) {
            var textNode = editor.getOrAddChildTextNode(cell);
            cellData.object = null;
            textNode.nodeValue = null;
        }
    }

    placeElementObject(cell, selection) {
        var textNode = this.getOrAddChildTextNode(cell);
        if (cell.cellData == null) {
            cell.cellData = new CellData();
        }
        cell.cellData.object = selection;
        textNode.nodeValue = selection;
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

    resetMap(map_size = MAP_SIZES.small) {
        table.innerHTML = "";
        this.map_size = map_size;
        document.getElementById(SELECT_ID_DROPDOWN_MAP_SIZES).value = findMapSizeDefinitionByValue(this.map_size);
        for (let i = 0; i < this.map_size; i++) {
            const tr = table.insertRow();
            for (let j = 0; j < this.map_size; j++) {
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

        function findMapSizeDefinitionByValue(value) {
            var result = null;
            for (const [key, sizeValue] of Object.entries(MAP_SIZES)) {
                if (sizeValue == value) {
                    result = key;
                }
            }
            return result;
        }
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