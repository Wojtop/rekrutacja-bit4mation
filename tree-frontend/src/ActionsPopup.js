import React from "react";
import "./App.css";
import {callDeleteNode} from "./helpers";

import 'reactjs-popup/dist/index.css';


export default function ActionsPopup(props) {
    const deleteNode = function(nodeId){
        console.log("Delete node: ", nodeId)
        callDeleteNode(nodeId, props.setData, props.setError, props.setLoading )
    }

    console.log("Node datum in component: ", props)
    const nodeDatum = props.nodeDatum;
    return (
        <div>
            <div>
                <h3>Value: {nodeDatum.name}</h3>
                <h4>ID: {nodeDatum.attributes?.id}</h4>
            </div>

            <div>

                <form name={"deleteNode"} action={""}>
                    <button name={"deleteButton"} onClick={()=>deleteNode(nodeDatum.attributes?.id)}>
                        Delete node and all its children
                    </button>
                </form>

            </div>
        </div>
    );
}