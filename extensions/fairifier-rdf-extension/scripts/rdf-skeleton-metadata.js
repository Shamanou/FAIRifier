/**
 * 
 */

function RdfSkeletonMetadataDialog(schema){    
    this._createDialog();
};

RdfSkeletonMetadataDialog.prototype._createDialog = function() {
    var self = this;
    var frame = DialogSystem.createDialog();
    
    frame.width("50px");
    
    var header = $('<div></div>').addClass("dialog-header").text("RDF Skeleton Metadata").appendTo(frame);
    var body = $('<div></div>').addClass("dialog-body").appendTo(frame);
    var footer = $('<div></div>').addClass("dialog-footer").appendTo(frame);
    
    this._constructFooter(footer);
    this._constructBody(body);
  
    this._level = DialogSystem.showDialog(frame);
    
    this._renderBody(body);
};


RdfSkeletonMetadataDialog.prototype._constructFooter = function(footer) {
    var self = this;
    
    $('<button></button>').addClass('button').html("&nbsp;&nbsp;Save&nbsp;&nbsp;").click(function() {
        var schema = self.getJSON();
        Refine.postProcess(
                "rdf-extension",
                "save-rdf-schema",
                {},
                { schema: JSON.stringify(schema) },
                {},
                {   
                    onDone: function() {
                        DialogSystem.dismissUntil(self._level - 1);
                        theProject.overlayModels.rdfSchema = schema;
                    }
                }
            );
    }).appendTo(footer);
    
    $('<button></button>').addClass('button').text("Cancel").click(function() {
        DialogSystem.dismissUntil(self._level - 1);
    }).appendTo(footer);
};

RdfSchemaAlignmentDialog.prototype._constructBody = function(body) {
     var self = this;   
     var self = this;
     $('<p>' +
                'Please fill in this form to store your RDF skeleton.' +
            '</p>').appendTo(body);
    
    var elmts = DOM.bind(html);
    
};
