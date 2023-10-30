import React from "react";
import "./App.css";
import {callDeleteNode, callEditNode} from "./helpers";

import 'reactjs-popup/dist/index.css';


export default function ActionsPopup(props) {
    const nodeDatum = props.nodeDatum;
    const deleteNode = function (nodeId) {
        console.log("Delete node: ", nodeId)
        callDeleteNode(nodeId, props.setData, props.setLoading)
    }

    const [newValue, setNewValue] = React.useState(nodeDatum.name)
    const [newParent, setNewParent] = React.useState(null)

    const handleSubmit = () => {
        const request =
            {
                nodeId: nodeDatum.attributes.id, value: newValue,
                parentId: newParent != null ? newParent : nodeDatum.attributes.parentId
            }
        callEditNode(request, props.setData)
        console.log("Edited node request: ", request)
    }

    console.log("Node datum in component: ", props)
    return (
        <div>
            <div>
                <h3>Value: {nodeDatum.name}</h3>
                {nodeDatum.attributes?.id !== undefined &&
                    <h4>ID: {nodeDatum.attributes?.id}</h4>
                }
            </div>

            <div>
                {nodeDatum.attributes?.id !== undefined &&
                    <form name={"editNode"} onSubmit={handleSubmit}>
                        <label>
                            <p>Change value: </p>
                            <input type={"number"} placeholder={"New value"} onChange={(v) => {
                                setNewValue(parseInt(v.target.value))
                            }}/>
                        </label>
                        <br/>
                        <label>
                            <p>Change parent: </p>
                            <input type={"number"} placeholder={"New parent ID"} onChange={(v) => {
                                setNewParent(parseInt(v.target.value))
                            }}/>
                        </label>
                        <br/>
                        <input type={"submit"} value={"Change"}/>
                    </form>
                }
            </div>
            <br/>

            <div>
                {nodeDatum.attributes?.id !== undefined &&
                    <form name={"deleteNode"} action={""}>
                        <button name={"deleteButton"} onClick={() => deleteNode(nodeDatum.attributes?.id)}>
                            Delete node and all its children
                        </button>
                    </form>
                }

            </div>
        </div>
    );
}